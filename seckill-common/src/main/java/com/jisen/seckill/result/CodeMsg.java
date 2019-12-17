package com.jisen.seckill.result;

/**
 * @author jisen
 * @date 2019/6/10 20:13
 */
public enum CodeMsg {

    /**
     * 通用异常
     */
    SUCCESS(0, "success"),
    SERVER_ERROR(500100, "服务端异常"),
    BIND_ERROR(500101, "参数校验异常：%s"),
    REQUEST_ILLEGAL(500102, "请求非法"),
    VERITF_FAIL(500103, "校验失败，请重新输入表达式结果或刷新校验码重新输入"),
    ACCESS_LIMIT_REACHED(500104, "访问太频繁！"),

    /**
     * 用户模块 5002XX
     */
    SESSION_ERROR(500210, "Session不存在或者已经失效，请返回登录！"),
    PASSWORD_EMPTY(500211, "登录密码不能为空"),
    MOBILE_EMPTY(500212, "手机号不能为空"),
    MOBILE_ERROR(500213, "手机号格式错误"),
    MOBILE_NOT_EXIST(500214, "手机号不存在"),
    PASSWORD_ERROR(500215, "密码错误"),
    USER_EXIST(500216, "用户已经存在，无需重复注册"),
    REGISTER_SUCCESS(500217, "注册成功"),
    REGISTER_FAIL(500218, "注册异常"),
    FILL_REGISTER_INFO(500219, "请填写注册信息"),
    WAIT_REGISTER_DONE(500220, "等待注册完成"),

    //登录模块 5002XX

    //商品模块 5003XX

    //订单模块 5004XX
    ORDER_NOT_EXIST(500400, "订单不存在"),

    /**
     * 秒杀模块 5005XX
     */
    SECKILL_OVER(500500, "商品已经秒杀完毕"),
    REPEATE_SECKILL(500501, "不能重复秒杀"),
    SECKILL_FAIL(500502, "秒杀失败"),
    SECKILL_PARM_ILLEGAL(500503, "秒杀请求参数异常：%s");

    private int code;
    private String msg;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 构造器定义为private是为了防止controller直接new
     *
     * @param code
     * @param msg
     */
    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 动态地填充msg字段
     *
     * @param args
     * @return
     */
//    public CodeMsg fillArgs(Object... args) {
//        int code = this.code;
//        String message = String.format(this.msg, args);// 将arg格式化到msg中，组合成一个message
//
//        return
//    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
