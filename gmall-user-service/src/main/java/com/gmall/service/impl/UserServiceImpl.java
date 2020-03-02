package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.UmsMember;
import com.gmall.mapper.UserMapper;
import com.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * UserServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 17时 27分 10秒
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UmsMember getMemberById(Integer id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id.toString());
        UmsMember umsMember1 = userMapper.selectOne(umsMember);
        return umsMember1;
    }
}