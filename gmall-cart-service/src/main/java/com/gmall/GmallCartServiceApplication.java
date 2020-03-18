package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * GmallCartServiceApplication
 *
 * @Author: theliar
 * @CreateTime: 2020-03-14 / 23时 31分 40秒
 * @Description:
 */
@SpringBootApplication
@MapperScan(basePackages = "com.gmall.mapper")
public class GmallCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCartServiceApplication.class,args);
    }
}