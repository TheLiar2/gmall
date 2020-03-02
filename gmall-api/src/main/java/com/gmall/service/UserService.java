package com.gmall.service;

import com.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

/**
 * UserService
 *
 * @Author: theliar
 * @CreateTime: 2020-02-29 / 22时 29分 14秒
 * @Description:
 */
public interface UserService{

    UmsMember getMemberById(Integer id);

}
