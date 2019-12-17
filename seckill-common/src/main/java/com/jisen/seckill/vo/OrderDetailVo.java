package com.jisen.seckill.vo;

import com.jisen.seckill.entity.OrderInfo;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/7/14 16:03
 */
public class OrderDetailVo implements Serializable {
    private UserVo user;
    private OrderInfo order;
    private GoodsVo goods;

    public void setUser(UserVo user) {
        this.user = user;
    }

    public UserVo getUser() {
        return user;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public GoodsVo getGoods() {
        return goods;
    }
}
