package com.gmall.user.controller;

import com.gmall.bean.UmsMember;
import com.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * UserController
 *
 * @Author: theliar
 * @CreateTime: 2020-02-29 / 22时 21分 01秒
 * @Description:
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @RequestMapping("/getMemberById/{id}")
    @ResponseBody
    public UmsMember getMemberById(@PathVariable("id")Integer id){
        System.out.println(id);
        //调用service
        UmsMember umsMember = userService.getMemberById(id);
        return umsMember;
    }

}