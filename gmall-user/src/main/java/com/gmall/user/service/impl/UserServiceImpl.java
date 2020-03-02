package com.gmall.user.service.impl;

import com.gmall.bean.UmsMember;
import com.gmall.service.UserService;
import com.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-02-29 / 22时 30分 31秒
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