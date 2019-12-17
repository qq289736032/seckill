package com.jisen.seckill.mvcconfig.intercepter;

import com.alibaba.fastjson.JSON;
import com.jisen.seckill.entity.SeckillUser;
import com.jisen.seckill.inteface.UserService;
import com.jisen.seckill.result.CodeMsg;
import com.jisen.seckill.result.Result;
import com.jisen.seckill.vo.UserVo;
import com.jisen.seckill.vo.profix.AccessKeyPrefix;
import com.jisen.seckill.vo.profix.SkUserKeyPrefix;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * 用户访问的拦截器，
 * 秒杀最重要的是从上流截拦请求，以缓解服务器以及数据库的压力，拦截器可以限制访问次数，防止过度刷屏
 * @author jisen
 * @date 2019/6/15 12:02
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    /**
     * 用于保存用户
     * 使用ThreadLocal保存用户，因为ThreadLocal是线程安全的，使用ThreadLocal可以保存当前线程持有的对象
     * 每个用户的请求对应一个线程，所以使用ThreadLocal以线程为键保存用户是合适的
     *
     */
    public static ThreadLocal<UserVo> userVoThreadLocal = new ThreadLocal<>();

    @Reference(interfaceClass = UserService.class)
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Reference(interfaceClass = RedisServiceApi.class)
//    RedisServiceApi redisService;

    /**
     * 目标方法执行前的处理
     * <p>
     * 查询访问次数，进行防刷请求拦截
     * 在 AccessLimit#seconds() 时间内频繁访问会有次数限制
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info(request.getRequestURL() + " 拦截请求");

        // 指明拦截的是方法
        if (handler instanceof HandlerMethod) {
            logger.info("HandlerMethod: " + ((HandlerMethod) handler).getMethod().getName());
            // 获取用户对象
            UserVo user = this.getUser(request, response);
            // 保存用户到ThreadLocal，这样，同一个线程访问的是同一个用户
            userVoThreadLocal.set(user);

            // 获取标注了 @AccessLimit 的方法，没有注解，则直接返回
            HandlerMethod hm = (HandlerMethod) handler;

            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);

            // 如果没有添加@AccessLimit注解，直接放行（true）
            if (accessLimit == null)
                return true;

            // 获取注解的元素值,取出访问间隔时间，最大访问次数，以及是否需要登录的状态
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxAccessCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    this.render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getPhone();
            } else {
                //do nothing
            }

            // 设置缓存过期时间
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);
            // 在redis中存储的访问次数，key为前缀+URI
            Integer count = (Integer)redisTemplate.opsForValue().get(accessKeyPrefix.getPrefix() + key);
            //Integer count = redisService.get(accessKeyPrefix, key, Integer.class);
            // 第一次重复点击 秒杀按钮
            if (count == null) {
                //这是第一次点击，保存值和间隔时间
                redisTemplate.opsForValue().set(accessKeyPrefix.getPrefix()+key,1,accessKeyPrefix.expireSeconds(),TimeUnit.SECONDS);
                //redisService.set(accessKeyPrefix, key, 1);
                // 点击次数未达最大值
            } else if (count < maxCount) {
                //为达到限制访问的次数，加1
                redisTemplate.opsForValue().increment(accessKeyPrefix.getPrefix()+key,1);
                //redisService.incr(accessKeyPrefix, key);
            } else {
                // 点击次数已满，返回操作频繁提示
                this.render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        // 不是方法直接放行
        return true;
    }

    /**
     * 渲染返回信息，??直接用流返回？
     * 以 json 格式返回
     *
     * @param response
     * @param cm
     * @throws Exception
     */
    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 和 UserArgumentResolver 功能类似，用于解析拦截的请求，这个方法用于获取 UserVo 对象
     *
     * @param request
     * @param response
     * @return UserVo 对象
     */
    private UserVo getUser(HttpServletRequest request, HttpServletResponse response) {
        logger.info(request.getRequestURL() + " 获取 UserVo 对象");


        // 从请求中获取token，对比cookie中和请求参数中的cookie是否一致?
        String paramToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        //如果请求参数中的token为空则取cookie中的
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;

        if (StringUtils.isEmpty(token)) {
            return null;
        }
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
     * 从众多的cookie中找出指定cookiName的cookie
     *
     * @param request
     * @param cookieName
     * @return cookiName对应的value
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return null;

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
        // 客户端cookie的有限期和缓存中的cookie有效期一致
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
