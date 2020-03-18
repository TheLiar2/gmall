package com.gmall.mapper;

import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * PmsProductSaleAttrMapper.xml
 *
 * @Author: theliar
 * @CreateTime: 2020-03-05 / 10时 44分 40秒
 * @Description:
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    //前端判断选中红框
    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySQL(@Param("spuId")String spuId,@Param("skuId")String skuId);


    //直接返回spu下所有sku的销售属性
    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(String productId);
}
