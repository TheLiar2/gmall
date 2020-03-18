package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.bean.PmsSkuSaleAttrValue;
import com.gmall.service.SkuService;
import com.gmall.service.SpuService;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemController
 *
 * @Author: theliar
 * @CreateTime: 2020-03-05 / 22时 52分 15秒
 * @Description:
 */
@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;


    @RequestMapping("index")
    public String index(){
        return "item";
    }

    @RequestMapping("{skuId}.html")
    public String getSkuInfo(@PathVariable("skuId")String skuId, Map<String,Object> map){
        System.out.println("正在查询skuId为"+skuId+"的信息");
        //第一步：调用skuService查询sku信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);
        //调用spuService查询sku对应的spu的销售属性和销售属性值，为了让前端显示不同销售属性值来回切换后成新的一种sku
        //这种方式前端页面所有的销售属性值都会被选中，如果确定每个sku对应的销售属性值呢？
        //List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getSpuSaleAttrListCheckBySkuDEProductId(pmsSkuInfo.getProductId());
        //第二步：通过sql查询，返回销售属性值的isChecked字段可用而不在是null。使用mybatis复杂查询，因为tk不能实现复杂查询
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getSpuSaleAttrListCheckBySQL(pmsSkuInfo);


        //第三步：获取可来回切换的sku的数据列表，
        List<PmsSkuInfo> pmsSkuInfos  = spuService.selectSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

//        生成一个类似map
//        258|259  112
//        258|260  113
        HashMap<String, Object> hashMap = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String key = "";
            for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
                if(i!=skuSaleAttrValueList.size()-1){
                    key += skuSaleAttrValueList.get(i).getSaleAttrValueId() + "|";
                }else{
                    key += skuSaleAttrValueList.get(i).getSaleAttrValueId();
                }
            }
            hashMap.put(key,skuInfo.getId());
        }

        String s = JSONObject.toJSONString(hashMap);
        System.out.println(s);


        map.put("skuInfo",pmsSkuInfo); //放sku信息
        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);//放sku对应spu的销售属性列表，并回显
        map.put("skuInfoSaleAttrValueList",s);//放sku对应spu已存在的所有sku的销售属性值列表，为了前端切换sku
        return "item";
    }


    @RequestMapping("{skuId}.html2")
    public String getSkuInfo2(@PathVariable("skuId")String skuId, Map<String,Object> map, HttpServletRequest httpServletRequest){
        System.out.println("正在查询skuId为"+skuId+"的信息");
        //第一步：调用skuService查询sku信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);
        //调用spuService查询sku对应的spu的销售属性和销售属性值，为了让前端显示不同销售属性值来回切换后成新的一种sku
        //这种方式前端页面所有的销售属性值都会被选中，如果确定每个sku对应的销售属性值呢？
        //List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getSpuSaleAttrListCheckBySkuDEProductId(pmsSkuInfo.getProductId());
        //第二步：通过sql查询，返回销售属性值的isChecked字段可用而不在是null。使用mybatis复杂查询，因为tk不能实现复杂查询
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getSpuSaleAttrListCheckBySQL(pmsSkuInfo);


        //第三步：获取可来回切换的sku的数据列表，
        List<PmsSkuInfo> pmsSkuInfos  = spuService.selectSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

//        生成一个类似map
//        258|259  112
//        258|260  113
        HashMap<String, Object> hashMap = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String key = "";
            for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
                if(i!=skuSaleAttrValueList.size()-1){
                    key += skuSaleAttrValueList.get(i).getSaleAttrValueId() + "|";
                }else{
                    key += skuSaleAttrValueList.get(i).getSaleAttrValueId();
                }
            }
            hashMap.put(key,skuInfo.getId());
        }

        String s = JSONObject.toJSONString(hashMap);
        System.out.println(s);

        String contextPath = httpServletRequest.getServletContext().getRealPath("/");
        System.out.println(contextPath);

        //以返回json文件的方式给前端调用
        //1.判断是否已存在对应的spu_xx的json文件
        //获取spu
        String spuId = pmsSkuInfo.getProductId();
        URL resource = ItemController.class.getClassLoader().getResource("static/spu/spu_"+spuId+".json");
        if(resource == null){
            String resource1 = ItemController.class.getClassLoader().getResource("static").getPath().substring(1);
            System.out.println(resource1);
            //不存在，使用io流进行创建
            File file = new File(resource1+"/spu");
            if(!file.exists()){
                file.mkdir();
            }
            File file1 = new File(resource1 + "/spu/spu_" + spuId + ".json");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                fileOutputStream.write(s.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //存在，不创建，继续下一步
        map.put("skuInfo",pmsSkuInfo); //放sku信息
        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);//放sku对应spu的销售属性列表，并回显

        //返回前端发送$.getJson时使用。
        map.put("spuId",spuId);
        return "item";
    }
}