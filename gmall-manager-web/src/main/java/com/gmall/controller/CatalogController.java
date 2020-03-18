package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;
import com.gmall.service.CatalogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CatalogController
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 22时 59分 01秒
 * @Description:
 */
@RestController
@CrossOrigin
public class CatalogController {

    @Reference
    private CatalogService catalogService;


    /**
     * 查询三级目录
     * @return
     */
    @RequestMapping("getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id){
        List<PmsBaseCatalog3> result = catalogService.getCatalog3(catalog2Id);
        return result;
    }

    /**
     * 查询二级目录
     * @return
     */
    @RequestMapping("getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id){
        List<PmsBaseCatalog2> result = catalogService.getCatalog2(catalog1Id);
        return result;
    }
    /**
     * 查询1级目录
     * @return
     */
    @RequestMapping("getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> result = catalogService.getCatalog1();
        return result;
    }

}