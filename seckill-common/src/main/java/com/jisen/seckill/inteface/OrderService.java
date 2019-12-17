package com.jisen.seckill.inteface;

import com.jisen.seckill.entity.OrderInfo;
import com.jisen.seckill.entity.SeckillOrder;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.vo.UserVo;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
public interface OrderService {
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(String userId, long goodsId);

    OrderInfo getOrderById(long orderId);


    OrderInfo saveSeckillOrder(UserVo user, GoodsVo goods);
}
