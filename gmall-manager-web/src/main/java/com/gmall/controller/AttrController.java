package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.service.AttrService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AttrController
 * 平台属性 --> 平台属性值
 *
 * @Author: theliar
 * @CreateTime: 2020-03-03 / 00时 01分 00秒
 * @Description:
 */
@RestController
@CrossOrigin
public class AttrController {

    @Reference
    private AttrService attrService;


    /**
     * 保存新建平台销售属性列表
     * @param pmsBaseAttrInfo
     * @return
     */
    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        System.out.println(pmsBaseAttrInfo);
        attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }


    /**
     * 通过三级id查询平台销售属性列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id){
        List<PmsBaseAttrInfo> result = attrService.getAttrInfoList(catalog3Id);
        return result;
    }
}