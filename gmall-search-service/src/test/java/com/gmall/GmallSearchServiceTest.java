package com.gmall;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsSearchSkuInfo;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * GmallSearchServiceTest
 *
 * @Author: theliar
 * @CreateTime: 2020-03-10 / 22时 12分 56秒
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallSearchServiceTest {

    @Autowired
    private JestClient jestClient;


    @Reference
    private SkuService skuService;


    @Test
    public void test() throws IOException, InvocationTargetException, IllegalAccessException {
        //测试是否能够ping通es
        Search search = new Search.Builder("{}").addIndex("gmall1015").addType("pmsSearchSkuInfo").build();
        SearchResult execute = jestClient.execute(search);
        System.out.println(execute.getTotal());
        System.out.println("测试成功=================");


        //往es中插入数据
        //调用skuService查询所有的skuInfo信息
        List<PmsSkuInfo> pmsSkuInfoList = skuService.findAll();
        System.out.println(pmsSkuInfoList);

        //skuInfo跟pmsSearchSkuInfo转化

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            //开始转换
            BeanUtils.copyProperties(pmsSearchSkuInfo,pmsSkuInfo);  // apache common utils
            //BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo); org.springframework.beans.BeanUtils
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        System.out.println("转换后长度："+pmsSearchSkuInfos.size());

        System.out.println("查询结束");

        //将pmsSearchSkuInfo插入es中
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            //封装成一个文档并保存
            Index build = new Index.Builder(pmsSearchSkuInfo).index("gmall1015").type("pmsSearchSkuInfo").id(pmsSearchSkuInfo.getId() + "").build();
            jestClient.execute(build);
        }

    }
}