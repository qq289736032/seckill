package com.jisen.seckillcommon.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/12 20:51
 */
@Data
@Getter
@Setter
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 2514325933983455213L;
    private Long id;
    private String userId;
    private Long orderId;
    private Long goodsId;

}
