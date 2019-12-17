package com.jisen.seckill.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/12 20:56
 */
@Data
public class RegisterVo implements Serializable {
    @NotNull
    private String phone;
    @NotNull
    private String nickname;

    private String head;
    @NotNull
    private String password;
}
