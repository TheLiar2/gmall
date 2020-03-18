package com.gmall.service;

import com.gmall.bean.*;

import java.util.List;
import java.util.Set;

/**
 * CatalogService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 06分 07秒
 * @Description:
 */
public interface AttrService {

    List<PmsBaseAttrInfo> getAttrInfoList(Integer catalog3Id);

    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);


    //根据返回的es数据查询对应的平台属性
    List<PmsBaseAttrInfo> queryBaseAttrInfoByAttrValueId(Set<String> valueIdsSet);


}