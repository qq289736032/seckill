package com.jisen.seckillorder.mapper;

import com.jisen.seckillcommon.entity.OrderInfo;
import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jisen
 * @date 2019/7/14 16:27
 */
@Mapper
public interface OrderMapper {

    SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long uuid, long goodsId);

    OrderInfo getOrderById(long orderId);

    GoodsVo getGoodsVoByGoodsId(Long goodsId);
}
