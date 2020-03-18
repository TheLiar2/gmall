package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.*;
import com.gmall.mapper.*;
import com.gmall.service.AttrService;
import com.gmall.service.CatalogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * CatalogServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 08分 26秒
 * @Description:
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private AttrInfoMapper attrInfoMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;


    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(Integer catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();

        pmsBaseAttrInfo.setCatalog3Id(catalog3Id.toString());

        List<PmsBaseAttrInfo> result = attrInfoMapper.select(pmsBaseAttrInfo);

        for (PmsBaseAttrInfo baseAttrInfo : result) {
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = attrValueMapper.select(pmsBaseAttrValue);
            //赋值给baseAttrInfo
            baseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }

        return result;
    }

    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        //先保存pmsBaseAttrInfo(需要返回主键)
        System.out.println("attrinfo的id："+pmsBaseAttrInfo.getId());
        attrInfoMapper.insertSelective(pmsBaseAttrInfo); //这个跟insert()区别是这个插入的时候如果是null值不会insert到数据库
        System.out.println("attrinfo的id："+pmsBaseAttrInfo.getId());

        //遍历属性值集合，然后保存
        for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            attrValueMapper.insertSelective(pmsBaseAttrValue);
        }

    }


    //根据返回的es数据查询对应的平台属性
    @Override
    public List<PmsBaseAttrInfo> queryBaseAttrInfoByAttrValueId(Set<String> valueIdsSet) {

        //将Set集合内容转为xx,xx,xx字符串数组
        String valueIdString = StringUtils.join(valueIdsSet, ",");

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoMapper.queryBaseAttrInfoByAttrValueId(valueIdString);
        return pmsBaseAttrInfos;
    }
}