package com.gmall.service;

import com.gmall.bean.*;

import java.util.List;

/**
 * SpuService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-04 / 12时 43分 53秒
 * @Description:
 */
public interface SkuService {

    //保存一个sku
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    //通过skuId查询一个sku信息
    PmsSkuInfo getSkuInfoBySkuId(String skuId);

    //用于es查到所有的pmsskuInfo，与pmsSearchSkuInfo作转换
    List<PmsSkuInfo> findAll();
}