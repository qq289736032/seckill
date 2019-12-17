package com.jisen.seckill.vo;

/**
 * @author jisen
 * @date 2019/6/29 18:50
 */
public class GoodsDetailVo {
    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private UserVo user;

    @Override
    public String toString() {
        return "GoodsDetailVo{" +
                "seckillStatus=" + seckillStatus +
                ", remainSeconds=" + remainSeconds +
                ", goods=" + goods +
                ", user=" + user +
                '}';
    }

    public int getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }
}
