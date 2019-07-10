package com.jisen.seckillorder.service;

import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.inteface.OrderService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long uuid, long goodsId) {
        return null;
    }
}
