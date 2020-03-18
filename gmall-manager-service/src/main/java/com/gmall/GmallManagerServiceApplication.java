package com.gmall;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * GmallManagerServiceApplication
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 05分 32秒
 * @Description:
 */
@SpringBootApplication
@MapperScan(basePackages = "com.gmall.mapper")
public class GmallManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallManagerServiceApplication.class,args);
    }
}