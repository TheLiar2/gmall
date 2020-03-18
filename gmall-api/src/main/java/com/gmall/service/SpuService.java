package com.gmall.service;

import com.gmall.bean.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SpuService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-04 / 12时 43分 53秒
 * @Description:
 */
public interface SpuService {

    //查询spu基本销售属性列表
    List<PmsBaseSaleAttr> getBaseSaleAttrList();

    //保存pmsProductInfo即一个spu
    void savePmsProductInfo(PmsProductInfo pmsProductInfo);

    //通过三级id查询三级id下的所有spu商品（不包括product_image。product_sale_attr,product_sale_attr_value）
    List<PmsProductInfo> spuListByCatalog3Id(Integer catalog3Id);

    //通过spuId查询spu对应的product_sale_attr(包络product_sale_attr_value)
    List<PmsProductSaleAttr> spuSaleAttrListBySpuId(Integer spuId);

    //通过spuId查询spu对应的product_image
    List<PmsProductImage> spuImageListBySpuId(Integer spuId);

    //调用spuService查询sku对应的spu的销售属性和销售属性值，为了让前端显示不同销售属性值来回切换后成新的一种sku
    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySkuDEProductId(String productId);

    //第二种查询sku对应的spu的销售属性和销售属性值，进行了优化，可以把isChecked查出来，前端就能定位到每个属性添加红框
    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySQL(PmsSkuInfo pmsSkuInfo);

    //获取可来回切换的sku的数据列表
    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String productId);
}