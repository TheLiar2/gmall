package com.gmall.service;

import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.bean.PmsSearchParam;
import com.gmall.bean.PmsSearchSkuInfo;

import java.util.List;
import java.util.Set;

/**
 * CatalogService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 06分 07秒
 * @Description:
 */
public interface SearchService {

    //根据条件查询es中的数据
    List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam);

}