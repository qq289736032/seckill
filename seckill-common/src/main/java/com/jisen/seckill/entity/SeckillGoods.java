package com.jisen.seckill.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jisen
 * @date 2019/6/12 20:50
 */
@Data
@Setter
@Getter
public class SeckillGoods implements Serializable {


    private static final long serialVersionUID = -2834572294761781369L;
    private Long id;
    private Long goodsId;
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
