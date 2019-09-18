package com.jisen.seckillcommon.inteface;

import com.jisen.seckillcommon.entity.OrderInfo;
import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.vo.GoodsVo;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
public interface OrderService {
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long uuid, long goodsId);

    OrderInfo getOrderById(long orderId);

}
