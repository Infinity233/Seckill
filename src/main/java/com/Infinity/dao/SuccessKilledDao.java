package com.Infinity.dao;

import com.Infinity.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {

    /**
     * 插入购买明细，通过组合主键过滤重复
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
