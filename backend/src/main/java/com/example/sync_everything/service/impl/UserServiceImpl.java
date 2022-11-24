package com.example.sync_everything.service.impl;

import com.example.sync_everything.dao.UserDao;
import com.example.sync_everything.entity.user.User;
import com.example.sync_everything.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ForeverDdB 835236331@qq.com
 * @ClassName UserServiceImpl
 * @Description
 * @createTime 2022年 09月03日 21:51
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User queryById(Long id) {
        return userDao.queryById(id);
    }

    @Override
    public User queryByUser(User user) {
        return userDao.queryByUser(user);
    }

    @Override
    public int createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userDao.deleteUser(id);
    }

    @Override
    public Boolean exists(Long id) {
        return userDao.exists(id);
    }
}
