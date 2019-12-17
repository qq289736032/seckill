package com.jisen.seckill.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jisen
 * @date 2019/6/12 21:15
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 7158619124863986979L;

    private String userId;
    private String phone;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
