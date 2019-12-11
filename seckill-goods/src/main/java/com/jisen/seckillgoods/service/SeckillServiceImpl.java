package com.jisen.seckillgoods.service;

import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.inteface.OrderService;
import com.jisen.seckillcommon.inteface.SeckillService;
import com.jisen.seckillcommon.vo.profix.SkKeyPrefix;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author jisen
 * @date 2019/7/7 14:31
 */
@Service
public class SeckillServiceImpl implements SeckillService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Reference(interfaceClass = OrderService.class)
    private OrderService orderServiceImpl;
    /**
     * 获取秒杀结果
     *
     * @param uuid
     * @param goodsId
     * @return
     */
    @Override
    public long getSeckillResult(String uuid, long goodsId) {
        SeckillOrder order = orderServiceImpl.getSeckillOrderByUserIdAndGoodsId(uuid, goodsId);
        if (order != null){
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1; 
            }else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        Boolean aBoolean = redisTemplate.hasKey(SkKeyPrefix.GOODS_SK_OVER.getPrefix() + goodsId);
        return aBoolean;
    }
}
