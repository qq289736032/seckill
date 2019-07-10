package com.jisen.seckillcommon.inteface;

import com.jisen.seckillcommon.entity.SeckillOrder;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
public interface OrderService {
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long uuid, long goodsId);
}
