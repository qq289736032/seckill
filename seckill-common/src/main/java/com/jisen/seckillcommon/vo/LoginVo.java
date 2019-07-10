package com.jisen.seckillcommon.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/12 21:15
 */
@Data
public class LoginVo implements Serializable {

    /**
     * 通过注解的方式校验手机号（JSR303）
     */
    @NotNull
    //@IsMobile// 自定义的注解完成手机号的校验
    private String mobile;

    /**
     * 通过注解的方式校验密码（JSR303）
     */
    @NotNull
    @Length(min = 32)// 长度最小为32
    private String password;
}
