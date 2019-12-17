package com.jisen.seckillweb.controller;
import com.jisen.seckillcommon.inteface.UserService;
import com.jisen.seckillcommon.result.CodeMsg;
import com.jisen.seckillcommon.exception.GlobalException;
import com.jisen.seckillcommon.result.Result;
import com.jisen.seckillcommon.vo.LoginVo;
import com.jisen.seckillcommon.vo.RegisterVo;
import com.jisen.seckillcommon.vo.UserInfoVo;
import com.jisen.seckillcommon.vo.profix.SkUserKeyPrefix;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author jisen
 * @date 2019/6/12 19:41
 */
@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 日志记录：Logger是由slf4j接口规范创建的，对象有具体的实现类创建
     */
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Reference(interfaceClass = UserService.class)
    private UserService userServiceImpl;

    /**
     * 由于需要将一个cookie对应的用户存入第三方缓存中，这里用redis，所以需要引入redis serice
     */
//    @Reference(interfaceClass = RedisServiceApi.class)
//    RedisServiceApi redisService;

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index() {
        logger.info("首页接口");
        return "login";// login页面
    }

    /**
     * 用户登录接口
     *
     * @param response 响应
     * @param loginVo  用户登录请求的表单数据（将表单数据封装为了一个Vo：Value Object）
     *                 注解@Valid用于校验表单参数，校验成功才会继续执行业务逻辑，否则，
     *                 请求参数校验不成功抛出异常
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Result login(HttpServletResponse response, @Valid LoginVo loginVo) {

        String token = userServiceImpl.login(loginVo);
        logger.info("token: " + token);

        // 将token写入cookie中, 然后传给客户端（一个cookie对应一个用户，这里将这个cookie的用户信息写入redis中）
        Cookie cookie = new Cookie(UserService.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());// 保持与redis中的session一致
        cookie.setPath("/");
        response.addCookie(cookie);

        // 返回登陆成功
        return Result.success(true);
    }


    /**
     * 注册跳转
     *
     * @return
     */
    @RequestMapping(value = "/doRegister",method = RequestMethod.GET)
    public String doRegister() {
        logger.info("doRegister()");
        return "register";
    }


    /**
     * 注册接口
     *
     * @param registerVo
     * @return
     */
    @PostMapping(value = "/register")
    @ResponseBody
    public Result<Boolean> register(RegisterVo registerVo) {
        logger.info("RegisterVo = " + registerVo);

        if (registerVo == null) {
            throw new GlobalException(CodeMsg.FILL_REGISTER_INFO);
        }

        CodeMsg codeMsg = userServiceImpl.register(registerVo);
        return Result.info(codeMsg);
    }
}
