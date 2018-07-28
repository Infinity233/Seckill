package com.Infinity.service.impl;

import com.Infinity.dao.SeckillDao;
import com.Infinity.dao.SuccessKilledDao;
import com.Infinity.dto.Exposer;
import com.Infinity.dto.SeckillExecution;
import com.Infinity.entity.Seckill;
import com.Infinity.entity.SuccessKilled;
import com.Infinity.enums.SeckillStatEnum;
import com.Infinity.exception.RepeatKillException;
import com.Infinity.exception.SeckillCloseException;
import com.Infinity.exception.SeckillException;
import com.Infinity.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SeckillDao seckillDao;

    private SuccessKilledDao successKilledDao;

    // md5盐值字符串，用于混淆MD5
    private final String salt = "d/as-d/*a-s/*%@H%K@JJK-fdef/a/h/h/u/yrDAS*/AS/*312312";

    @Autowired
    public SeckillServiceImpl(SeckillDao seckillDao, SuccessKilledDao successKilledDao) {
        this.seckillDao = seckillDao;
        this.successKilledDao = successKilledDao;
    }

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        Seckill seckill = getById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串的过程， 不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {

        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill date rewrite");
        }

        // 执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                // 没有更新到记录，秒杀结束 or 没库存了
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 减库存成功， 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException | RepeatKillException e1) {
            logger.error(e1.getMessage(), e1);
            throw e1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            // 所有编译器异常，转换为运行时异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
