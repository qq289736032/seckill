package com.jisen.seckill.service;

import com.jisen.seckill.entity.OrderInfo;
import com.jisen.seckill.entity.SeckillOrder;
import com.jisen.seckill.inteface.GoodsService;
import com.jisen.seckill.inteface.OrderService;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.vo.UserVo;
import com.jisen.seckill.vo.profix.OrderKeyPrefix;
import com.jisen.seckill.vo.profix.SkKeyPrefix;
import com.jisen.seckill.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Reference(interfaceClass = GoodsService.class)
    private GoodsService goodsServiceImpl;

    @Override
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(String userId, long goodsId) {
        return orderMapper.getSeckillOrderByUserIdAndGoodsId(userId,goodsId);
    }

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    /**
     * 1.减库存 2.写入订单 3.写入秒杀订单
     * @param user
     * @param goods
     */
    @Override
    @Transactional
    public OrderInfo saveSeckillOrder(UserVo user, GoodsVo goods) {
        // 1. 减库存
        boolean success = goodsServiceImpl.reduceStock(goods.getId());
        //减不成功直接return，后面的就不会操作
        if (!success) {
            redisTemplate.opsForValue().set(SkKeyPrefix.GOODS_SK_OVER+"" + goods.getId(), true);
            return null;
        }

        // 2. 生成订单；向 order_info 表和 seckill_order 表中写入订单信息
        OrderInfo orderInfo = new OrderInfo();
        SeckillOrder seckillOrder = new SeckillOrder();

        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);// 订单中商品的数量
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());// 秒杀价格
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getUserId());

        // 将订单信息插入 order_info 表中
        long orderId = orderMapper.insert(orderInfo);
        log.debug("将订单信息插入 order_info 表中: 记录为" + orderId);

        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getUserId());

        // 将秒杀订单插入 seckill_order 表中
        orderMapper.insertSeckillOrder(seckillOrder);
        log.debug("将秒杀订单插入 seckill_order 表中");

        // 将秒杀订单概要信息存储于redis中
//        redisService.set(OrderKeyPrefix.getSeckillOrderByUidGid, ":" + user.getUuid() + "_" + goods.getId(), seckillOrder);
        redisTemplate.opsForValue().set(OrderKeyPrefix.SK_ORDER+ ":" + user.getUserId() + "_" + goods.getId(), seckillOrder);

        return orderInfo;
    }

}
