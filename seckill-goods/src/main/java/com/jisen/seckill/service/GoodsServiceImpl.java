package com.jisen.seckill.service;

import com.jisen.seckill.inteface.GoodsService;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.mapper.GoodsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author jisen
 * @date 2019/6/29 20:04
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> listGoods() {
        return goodsMapper.listGoods();
    }

    @Override
    public GoodsVo getGoodsVoById(long goodsId) {
        return goodsMapper.getGoodsVoById(goodsId);
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.getGoodsVoById(goodsId);
    }

    /**
     * 减库存，是真的减
     * @param goodsId
     * @return
     */
    @Override
    public boolean reduceStock(Long goodsId) {
        int ret = goodsMapper.reduceStack(goodsId);
        return ret > 0;
    }
}
