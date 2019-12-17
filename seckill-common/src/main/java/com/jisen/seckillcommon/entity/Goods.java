package com.jisen.seckillcommon.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/12 20:40
 */
@Data
@Getter
@Setter
@ToString
public class Goods implements Serializable {

    private static final long serialVersionUID = 2372064155862817752L;
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Long goodsStock;
}
