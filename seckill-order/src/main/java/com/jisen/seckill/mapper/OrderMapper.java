package com.jisen.seckill.mapper;

import com.jisen.seckill.entity.OrderInfo;
import com.jisen.seckill.entity.SeckillOrder;
import com.jisen.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.*;

/**
 * @author jisen
 * @date 2019/7/14 16:27
 */
@Mapper
public interface OrderMapper {
    @Select("select * from seckill_order where user_id=#{userId} AND goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId")String userId, @Param("goodsId")Long goodsId);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(@Param("orderId")Long orderId);

    GoodsVo getGoodsVoByGoodsId(Long goodsId);

    /**
     * 将订单信息插入 order_info 表中
     *
     * @param orderInfo 订单信息
     * @return 插入成功的订单信息id
     */
    @Insert("INSERT INTO order_info (user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)"
            + "VALUES (#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    // 查询出插入订单信息的表id，并返回非自增主键，可表里设置的是自增主键(bigint),为了返回的是long类型使用@SelectKey
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "SELECT last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 将秒杀订单信息插入到 seckill_order 中
     *
     * @param seckillOrder 秒杀订单
     */
    @Insert("INSERT INTO seckill_order(user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);
}
