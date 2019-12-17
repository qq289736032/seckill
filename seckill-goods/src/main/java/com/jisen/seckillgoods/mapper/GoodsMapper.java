package com.jisen.seckillgoods.mapper;

import com.jisen.seckillcommon.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author jisen
 * @date 2019/7/2 18:53
 */
@Mapper
public interface GoodsMapper {

    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id")
    List<GoodsVo> listGoods();
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoById(@Param("goodsId") long goodsId);

    @Update("UPDATE seckill_goods SET stock_count=stock_count-1 WHERE goods_id=#{goodsId} AND stock_count>0 ")
    int reduceStack(@Param("goodsId") Long goodsId);
}
