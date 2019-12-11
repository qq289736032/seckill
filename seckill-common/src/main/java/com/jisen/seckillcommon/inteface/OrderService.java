package com.jisen.seckillcommon.inteface;

import com.jisen.seckillcommon.entity.OrderInfo;
import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.vo.GoodsVo;
import com.jisen.seckillcommon.vo.UserVo;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
public interface OrderService {
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(String userId, long goodsId);

    OrderInfo getOrderById(long orderId);


    OrderInfo saveSeckillOrder(UserVo user, GoodsVo goods);
}
