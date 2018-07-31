package com.Infinity.dao.cache;

import com.Infinity.entity.Seckill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(Long seckillId) {
        // redis操作逻辑
        try (Jedis jedis = jedisPool.getResource()) {

            String key = "seckill:" + seckillId;
            // 并没有实现内部序列化操作
            // get->byte[] -> 反序列化 ->Object(Seckill)
            // 采用自定义序列化
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                Seckill seckill = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                // seckill 被反序列化
                return seckill;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {

        // set Object(Seckill) -> 序列化 -> byte[]

        try (Jedis jedis = jedisPool.getResource()) {

            String key = "seckill:" + seckill.getSeckillId();
            byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

            int timeout = 60 * 60;
            return jedis.setex(key.getBytes(), timeout, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
