package com.jisen.seckillcommon.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jisen
 * @date 2019/6/12 20:48
 */
@Data
@Getter
@Setter
public class OrderInfo implements Serializable {
    private Long id;
    private String userId;
    private Long goodsId;
    private Long deliveryAddrId;
    private String goodsName;
    private Integer goodsCount;
    private Double goodsPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;

}
