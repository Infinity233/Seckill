package com.Infinity.dao;

import com.Infinity.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return  如果影响行数>1，表示更新行数
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    Seckill queryById(long seckillId);

    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit")int limit);

}
