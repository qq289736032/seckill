package com.jisen.seckilluser.mapper;

import com.jisen.seckillcommon.entity.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author jisen
 * @date 2019/6/16 13:55
 */
@Mapper
public interface SeckillUserMapper {
    /**
     * 通过 phone 查询用户信息
     *
     * @param phone
     * @return
     */
    SeckillUser getUserByPhone(@Param("phone") String phone);

    /**
     * 更新用户信息
     *
     * @param updatedUser
     */
    @Update("UPDATE seckill_user SET password=#{password} WHERE id=#{id}")
    void updatePassword(SeckillUser updatedUser);


    /**
     * 插入一条用户信息到数据库中
     *
     * @param seckillUser
     * @return
     */
    long insertUser(SeckillUser seckillUser);

    /**
     * 查询电话号码
     *
     * @param phone
     * @return
     */
    long findPhone(long phone);

}
