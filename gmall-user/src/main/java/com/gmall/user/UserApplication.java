package com.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * UserApplication
 *
 * @Author: theliar
 * @CreateTime: 2020-02-29 / 22时 19分 40秒
 * @Description:
 */
@SpringBootApplication
/*扫描mapper接口*/
@MapperScan(basePackages = "com.gmall.user.mapper")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}