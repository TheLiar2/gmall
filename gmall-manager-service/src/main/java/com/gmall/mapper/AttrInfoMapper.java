package com.gmall.mapper;

import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.bean.PmsBaseCatalog1;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * CatalogMapper1
 *
 * @Author: theliar
 * @CreateTime: 2020-03-02 / 23时 15分 37秒
 * @Description:
 */
public interface AttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    //根据返回的es数据查询对应的平台属性
    List<PmsBaseAttrInfo> queryBaseAttrInfoByAttrValueId(@Param("valueIdString") String valueIdString);
}