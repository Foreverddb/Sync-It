package com.example.sync_everything.dao;

import com.example.sync_everything.entity.user.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author ForeverDdB 835236331@qq.com
 * @ClassName UserDao
 * @Description
 * @createTime 2022年 09月03日 21:42
 **/
public interface UserDao {

    User queryById(@Param("id") Long id);

    User queryByUser(User user);

    int createUser(User user);

    int updateUser(User user);

    int deleteUser(@Param("id") Long id);

    Boolean exists(@Param("id") Long id);

}

