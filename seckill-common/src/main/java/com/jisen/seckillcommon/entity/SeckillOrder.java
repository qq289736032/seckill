package com.jisen.seckillcommon.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jisen
 * @date 2019/6/12 20:51
 */
@Data
@Getter
@Setter
public class SeckillOrder {

    private Long id;
    private String userId;
    private Long orderId;
    private Long goodsId;

}
