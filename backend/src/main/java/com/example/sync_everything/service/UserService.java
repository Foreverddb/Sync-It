package com.example.sync_everything.service;

import com.example.sync_everything.entity.user.User;

/**
 * @author ForeverDdB 835236331@qq.com
 * @ClassName UserService
 * @Description
 * @createTime 2022年 09月03日 21:50
 **/
public interface UserService {

    User queryById(Long id);

    User queryByUser(User user);

    int createUser(User user);

    int updateUser(User user);

    int deleteUser(Long id);

    Boolean exists(Long id);

}
