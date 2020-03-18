package com.gmall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * ItemControllerTest
 *
 * @Author: theliar
 * @CreateTime: 2020-03-08 / 16时 29分 41秒
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemControllerTest {

    @Test
    public void test(){

//        try {
//            String serverpath= ResourceUtils.getURL("classpath:/static/spu").getPath();
//            System.out.println(serverpath);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //测试获取文件下是否存在某个资源
        URL resource1 = ItemControllerTest.class.getClassLoader().getResource("static/js");
        String resource2 = ItemControllerTest.class.getClassLoader().getResource("static").getPath();
        System.out.println(resource1);
        System.out.println(resource2);
    }
}