package com.jisen.seckill.mvcconfig;

import com.alibaba.fastjson.JSONObject;
import com.jisen.seckill.entity.SeckillUser;
import com.jisen.seckill.inteface.UserService;
import com.jisen.seckill.vo.UserVo;
import com.jisen.seckill.vo.profix.SkUserKeyPrefix;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解析新请求，并将请求的参数设置到方法参数中
 * @author jisen
 * @date 2019/6/14 19:25
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private static Logger logger = LoggerFactory.getLogger(UserArgumentResolver.class);

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 由于需要将一个cookie对应的用户存入第三方缓存中，这里用redis，所以需要引入redis service
     */
//    @Reference(interfaceClass = RedisServiceApi.class)
//    RedisServiceApi redisService;

    /**
     * 当请求参数为 UserVo 时，使用这个解析器处理
     * 客户端的请求到达某个 Controller 的方法时，判断这个方法的参数是否为 UserVo，
     * 如果是，则这个 UserVo 参数对象通过下面的 resolveArgument() 方法获取，
     * 然后，该 Controller 方法继续往下执行时所看到的 UserVo 对象就是在这里的 resolveArgument() 方法处理过的对象
     *
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        logger.info("supportsParameter");
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == UserVo.class;
    }

    /**
     * 从分布式 session 中获取 UserVo 对象
     *
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 获取请求和响应对象
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        logger.info(request.getRequestURL() + " resolveArgument");

        // 从请求对象中获取token（token可能有两种方式从客户端返回，1：通过url的参数，2：通过set-Cookie字段）
        String paramToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME_TOKEN);

        //如果都为空
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        // 判断是哪种方式返回的token，并由该种方式获取token（cookie）
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        // 通过token就可以在redis中查出该token对应的用户对象

        //从redis中获取user
        SeckillUser seckillUser = (SeckillUser)redisTemplate.opsForValue().get(SkUserKeyPrefix.TOKEN.getPrefix() + token);
        //UserVo userVo = redisService.get(SkUserKeyPrefix.TOKEN, token, UserVo.class);

        UserVo userVo = null;
        if (seckillUser != null){
            userVo = new UserVo();
            userVo.setPhone(seckillUser.getPhone());
            userVo.setNickname(seckillUser.getNickname());
            userVo.setUserId(seckillUser.getUserId());
            userVo.setHead(seckillUser.getHead());
            userVo.setLoginCount(seckillUser.getLoginCount());
            userVo.setLastLoginDate(seckillUser.getLastLoginDate());
            userVo.setRegisterDate(seckillUser.getRegisterDate());
            userVo.setSalt(seckillUser.getSalt());
            addCookie(response, token, seckillUser);
        }

        // 在有效期内从redis获取到key之后，需要将key重新设置一下，从而达到延长有效期的效果

        return userVo;
    }

    /**
     * 根据cookie名获取相应的cookie值
     *
     * @param request
     * @param cookieName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        logger.info("getCookieValue");
        Cookie[] cookies = request.getCookies();
        // null判断，否则并发时会发生异常
        if (cookies == null || cookies.length == 0) {
            logger.info("cookies is null");
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 将cookie存入redis，并将cookie写入到请求的响应中
     *
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {

        redisTemplate.opsForValue().set(SkUserKeyPrefix.TOKEN.getPrefix()+token, user);

        //redisService.set(SkUserKeyPrefix.TOKEN, token, user);

        Cookie cookie = new Cookie(UserService.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
