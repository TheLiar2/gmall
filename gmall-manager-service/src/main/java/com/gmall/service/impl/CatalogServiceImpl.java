package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;
import com.gmall.mapper.CalalogMapper3;
import com.gmall.mapper.CatalogMapper1;
import com.gmall.mapper.CatalogMapper2;
import com.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * CatalogServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 08分 26秒
 * @Description:
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private CatalogMapper1 catalogMapper1;

    @Autowired
    private CatalogMapper2 catalogMapper2;

    @Autowired
    private CalalogMapper3 calalogMapper3;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = catalogMapper1.selectAll();
        return pmsBaseCatalog1s;
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {

        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id.toString());

        List<PmsBaseCatalog2> pmsBaseCatalog2s = catalogMapper2.select(pmsBaseCatalog2);
        return pmsBaseCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id) {

        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id.toString());

        List<PmsBaseCatalog3> pmsBaseCatalog3s = calalogMapper3.select(pmsBaseCatalog3);
        return pmsBaseCatalog3s;
    }
}