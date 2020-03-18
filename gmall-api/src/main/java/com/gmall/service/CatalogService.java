package com.gmall.service;

import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * CatalogService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 06分 07秒
 * @Description:
 */
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id);
}