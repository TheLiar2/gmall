package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * SkuController
 *
 * @Author: theliar
 * @CreateTime: 2020-03-05 / 18时 47分 18秒
 * @Description:
 */
@RestController
@CrossOrigin
public class SkuController {


    @Reference
    private SkuService skuService;


    /**
     * 保存一个sku
     * @param pmsSkuInfo
     * @return
     */
    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        //前端传的是spuId，实体是productId，需要在各位的set方法赋值给对方
        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }

}