package com.jisen.seckillorder.service;

import com.jisen.seckillcommon.entity.OrderInfo;
import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.inteface.OrderService;
import com.jisen.seckillcommon.vo.GoodsVo;
import com.jisen.seckillorder.mapper.OrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jisen
 * @date 2019/7/7 13:36
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Override
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long uuid, long goodsId) {
        return orderMapper.getSeckillOrderByUserIdAndGoodsId(uuid,goodsId);
    }

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }


}
