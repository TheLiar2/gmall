package com.gmall;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.pool.Connection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * GmallManagerWebTest
 *
 * @Author: theliar
 * @CreateTime: 2020-03-03 / 22时 16分 54秒
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallManagerWebTest {


    /**
     * 测试java客户端上传fastdfs图片
     * @throws IOException
     * @throws MyException
     */
    @Test
    public void test1() throws IOException, MyException {
        System.out.println("hahah");
        //java客户端测试是否能够上传图片到fastdfs
        String path = GmallManagerWebTest.class.getResource("/tracker.conf").getPath();

        //fastdfs初始化完成
        ClientGlobal.init(path);

        //连接tracker
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getTrackerServer();

        //获取storage
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //上传文件
        String[] strings = storageClient.upload_file("C:\\Users\\theliar\\Desktop\\timg.jpg","jpg",null);

        //遍历返回结果
        String filePath = "http://192.168.10.100";
        for (String string : strings) {
            filePath = filePath + "/" + string;
        }

        System.out.println(filePath+"---------------------");



    }
}