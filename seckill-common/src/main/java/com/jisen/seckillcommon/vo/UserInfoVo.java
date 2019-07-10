package com.jisen.seckillcommon.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/12 21:14
 */
@Data
public class UserInfoVo implements Serializable {
    private Integer uuid;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private int sex;
    private String birthday;
    private String lifeState;
    private String biography;
    private String address;
    private String headAddress;
    private long beginTime; // 创建时间
    private long updateTime;// 更新时间
}
