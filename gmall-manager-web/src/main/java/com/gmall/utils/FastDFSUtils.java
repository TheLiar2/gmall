package com.gmall.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FastDFSUtils
 *
 * @Author: theliar
 * @CreateTime: 2020-03-03 / 23时 09分 39秒
 * @Description:
 */
public class FastDFSUtils {

    static {
        //java客户端测试是否能够上传图片到fastdfs
        String path = FastDFSUtils.class.getResource("/tracker.conf").getPath();
        //fastdfs初始化完成
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String upload(MultipartFile file) throws IOException, MyException {
        System.out.println("uploading...");

        //连接tracker
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getTrackerServer();

        //获取storage
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //上传文件
        String originalFilename = file.getOriginalFilename(); // 获取上传的文件名
        int i = originalFilename.indexOf(".");
        String fileType = originalFilename.substring(i); //获取文件类型 ,如jpg


        String[] strings = storageClient.upload_file(file.getBytes(), fileType, null);

        //遍历返回结果
        String filePath = "http://192.168.10.100";
        for (String string : strings) {
            filePath = filePath + "/" + string;
        }

        System.out.println(filePath+"---------------------");

        return filePath;
    }

}