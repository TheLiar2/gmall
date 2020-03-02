package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * GmallUserServiceApplication
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 17时 24分 21秒
 * @Description:
 */
@SpringBootApplication
//通过mapper的接口扫描  只有service节点要
@MapperScan(basePackages = "com.gmall.mapper")
public class GmallUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserServiceApplication.class,args);
    }
}