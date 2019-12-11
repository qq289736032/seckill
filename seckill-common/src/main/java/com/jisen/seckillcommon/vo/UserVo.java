package com.jisen.seckillcommon.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jisen
 * @date 2019/6/12 21:15
 */
@Data
public class UserVo implements Serializable {
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
