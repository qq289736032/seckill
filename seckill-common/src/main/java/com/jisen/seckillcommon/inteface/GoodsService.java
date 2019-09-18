package com.jisen.seckillcommon.inteface;

import com.jisen.seckillcommon.vo.GoodsVo;

import java.util.List;

/**
 * @author jisen
 * @date 2019/6/29 18:28
 */
public interface GoodsService {
    List<GoodsVo> listGoods();

    GoodsVo getGoodsVoById(long goodsId);

    GoodsVo getGoodsVoByGoodsId(Long goodsId);
}
