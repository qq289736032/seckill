package com.jisen.seckilluser.service;


import com.jisen.seckilluser.entity.SeckillUser;
import com.jisen.seckillcommon.exception.GlobalException;
import com.jisen.seckillcommon.inteface.UserService;
import com.jisen.seckillcommon.redislock.RedisLock;
import com.jisen.seckillcommon.result.CodeMsg;
import com.jisen.seckillcommon.util.MD5Util;
import com.jisen.seckillcommon.util.UUIDUtil;
import com.jisen.seckillcommon.vo.LoginVo;
import com.jisen.seckillcommon.vo.RegisterVo;
import com.jisen.seckillcommon.vo.UserInfoVo;
import com.jisen.seckillcommon.vo.UserVo;
import com.jisen.seckillcommon.vo.profix.SkUserKeyPrefix;
import com.jisen.seckilluser.mapper.SeckillUserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

/**
 * 关于用户的业务service
 * @author jisen
 * @date 2019/6/12 21:19
 */
@Service
@Component
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = true)
    private RedisLock redisLockImpl;
    @Autowired
    private SeckillUserMapper seckillUserMapper;

    /**
     * ??
     * @param username
     * @param password
     * @return
     */
    @Override
    public int login(String username, String password) {
        return 45;
    }

    /**
     * 注册用户,注册接口为什么要加锁呢
     * @param userModel
     * @return
     */
    @Override
    public CodeMsg register(RegisterVo userModel) {
        //加锁
        String uniqueValue = UUID.randomUUID().toString().replace("-","") + "-" + Thread.currentThread().getId();
        String lockKey = "redis-lock";
        boolean lock = redisLockImpl.lock(lockKey, uniqueValue, 60 * 1000);
        if(!lock){
            return CodeMsg.WAIT_REGISTER_DONE;
        }
        logger.debug("注册接口加锁成功");

        // 检查用户是否注册
        SeckillUser user = this.getSeckillUserByPhone(userModel.getPhone());
        if(user!=null){
            return CodeMsg.USER_EXIST;
        }
        //生成skuser对象
        SeckillUser seckillUser = new SeckillUser();
        seckillUser.setPhone(userModel.getPhone());
        seckillUser.setNickname(userModel.getNickname());
        seckillUser.setHead(userModel.getHead());

        seckillUser.setSalt(MD5Util.SALT);

        String dbPass = MD5Util.formPassToDbPass(userModel.getPassword(), MD5Util.SALT);
        seckillUser.setPassword(dbPass);

        Date date = new Date();
        seckillUser.setRegisterDate(date);


        boolean unlock = redisLockImpl.unlock(lockKey, uniqueValue);
        if (!unlock)
            return CodeMsg.REGISTER_FAIL;
        logger.debug("注册接口解锁成功");

        // 写入数据库
        long id = seckillUserMapper.insertUser(seckillUser);
        // 用户注册成功
        if (id > 0)
            return CodeMsg.SUCCESS;
        // 用户注册失败
        return CodeMsg.REGISTER_FAIL;
    }

    @Override
    public boolean checkUsername(String username) {
        return false;
    }

    @Override
    public UserInfoVo getUserInfo(int uuid) {
        return null;
    }

    @Override
    public UserInfoVo updateUserInfo(UserInfoVo userInfoVo) {
        return null;
    }

    @Override
    public String login(@Valid LoginVo loginVo) {
        logger.info("登录操作{}",loginVo.toString());
        //获取用户提交的手机号码和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //判断手机号是否存在（先从缓存中取再从数据库取）
        SeckillUser seckillUserByPhone = this.getSeckillUserByPhone(Long.parseLong(mobile));
        if(seckillUserByPhone==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        logger.info("{}",seckillUserByPhone.toString());

        //判断手机号对应的密码是否一致
        String dbpassword = seckillUserByPhone.getPassword();
        String dbsalt = seckillUserByPhone.getSalt();
        String saltPassword = MD5Util.formPassToDbPass(password, dbsalt);
        if(!saltPassword.equals(dbpassword)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //登录成功
        String token = UUIDUtil.uuid();
        redisTemplate.opsForValue().set(SkUserKeyPrefix.TOKEN+token,seckillUserByPhone);

        return token;
    }

    @Override
    public UserVo getUserByPhone(long phone) {

        UserVo userVo = new UserVo();
        SeckillUser user = seckillUserMapper.getUserByPhone(phone);

        userVo.setUuid(user.getUuid());
        userVo.setSalt(user.getSalt());
        userVo.setRegisterDate(user.getRegisterDate());
        userVo.setPhone(user.getPhone());
        userVo.setPassword(user.getPassword());
        userVo.setNickname(user.getNickname());
        userVo.setLoginCount(user.getLoginCount());
        userVo.setLastLoginDate(user.getLastLoginDate());
        userVo.setHead(user.getHead());

        return userVo;
    }

    public SeckillUser getSeckillUserByPhone(Long phone) {
        //从redis中获取用户缓存数据
        SeckillUser seckillUser = (SeckillUser) redisTemplate.opsForValue().get(SkUserKeyPrefix.SK_USER_PHONE.getPrefix() + "_" + phone);
        if(seckillUser!=null){
            return seckillUser;
        }

        // 2. 如果缓存中没有用户数据，则从数据库中查询数据并将数据写入缓存
        // 先从数据库中取出数据
        SeckillUser userByPhone = seckillUserMapper.getUserByPhone(phone);
        // 然后将数据返回并将数据缓存在redis中
        if (userByPhone != null){
            redisTemplate.opsForValue().set(SkUserKeyPrefix.SK_USER_PHONE.getPrefix()+ "_" + phone,userByPhone);
            //redisService.set(SkUserKeyPrefix.SK_USER_PHONE, "_" + phone, user);
        }

        return userByPhone;
    }
}
