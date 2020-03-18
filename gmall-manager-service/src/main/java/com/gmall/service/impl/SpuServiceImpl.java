package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.*;
import com.gmall.mapper.*;
import com.gmall.service.SkuService;
import com.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * SpuServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-04 / 12时 44分 43秒
 * @Description:
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;


    // spu mapper
    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    // spu 销售属性
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    // spu 销售属性值
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    // spu 图片
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;


    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;


    @Override
    //查询spu基本销售属性列表
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        //调用spu基本销售属性mapper
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrs;
    }


    @Override
    //保存pmsProductInfo即一个spu
    public void savePmsProductInfo(PmsProductInfo pmsProductInfo) {
        // 1. 保存到pms_product_info表
        pmsProductInfoMapper.insertSelective(pmsProductInfo);
        System.out.println("spu保存成功,id:"+pmsProductInfo.getId());
        // 2. 保存pms_product_sale_attr
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            //保存pms_product_sale_attr
            pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
            System.out.println("spu销售属性保存成功,id:"+pmsProductSaleAttr.getId());
            // 3. 保存pms_product_sale_attr_value
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());
                //前台返回的4个基本销售属性的id赋值给pmsPrudctSaleAttrList了,所以pmsPrudctSaleAttrValueList的每个元素的attr_id要重新赋值下
                //不需要在set，前端返回时已经有的了
                //pmsProductSaleAttrValue.setSaleAttrId(pmsProductSaleAttr.getSaleAttrId());
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
                System.out.println("spu的销售属性值保存成功,id:"+pmsProductSaleAttrValue.getId());
            }
        }
        // 4. 保存pms_product_image
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(pmsProductInfo.getId());
            pmsProductImageMapper.insertSelective(pmsProductImage);
            System.out.println("spu image保存成功,id:"+pmsProductImage.getId());
        }

    }

    //通过三级id查询三级id下的所有spu商品（不包括product_image。product_sale_attr,product_sale_attr_value）
    @Override
    public List<PmsProductInfo> spuListByCatalog3Id(Integer catalog3Id) {
        //1.查询所有spu信息
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id.toString());
        List<PmsProductInfo> pmsProductInfoList = pmsProductInfoMapper.select(pmsProductInfo);
        System.out.println("pmsProductInfoLis长度:"+pmsProductInfoList.size());
        //返回的spu不需要其他三个内容信息
//        //2.遍历每个spu，将每个spu对应的image，sale_attr和sale_attr_value查出来
//        for (PmsProductInfo productInfo : pmsProductInfoList) {
//            //2.1 先查image
//            PmsProductImage pmsProductImage = new PmsProductImage();
//            pmsProductImage.setProductId(productInfo.getId());
//            List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
//            //将image赋值给productInfo
//            productInfo.setSpuImageList(pmsProductImages);
//
//            //2.2 查询sale_attr
//            PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//            pmsProductSaleAttr.setProductId(productInfo.getId());
//            List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
//            for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
//                //2.3 查询每个sale_attr对应的sale_attr_value
//                PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//                pmsProductSaleAttrValue.setProductId(productInfo.getId());
//                pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
//                List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//                //赋值给productSaleAttr
//                productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//            }
//            //赋值给productInfo
//            productInfo.setSpuSaleAttrList(pmsProductSaleAttrs);
//        }

        return pmsProductInfoList;
    }

    //通过spuId查询spu对应的product_sale_attr(包络product_sale_attr_value)
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListBySpuId(Integer spuId) {
            //1.通过spuId查询所有的product_sale_attr
            PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
            pmsProductSaleAttr.setProductId(spuId.toString());
            List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
            for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
                //2.查询每个product_sale_attr对应的所有product_sale_attr_value
                PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
                pmsProductSaleAttrValue.setProductId(spuId.toString());
                pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
                List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
                //赋值给productSaleAttr
                productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
            }
            return pmsProductSaleAttrs;
    }

    //通过spuId查询spu对应的product_image
    @Override
    public List<PmsProductImage> spuImageListBySpuId(Integer spuId)
    {
        //1.通过spuId查询spu对应的product_image
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId.toString());
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    //调用spuService查询sku对应的spu的销售属性和销售属性值，为了让前端显示不同销售属性值来回切换后成新的一种sku
    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySkuDEProductId(String productId) {
        //1.查询对应pmsProdcutSaleAttr
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(productId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        System.out.println("pmsProductSaleAttrs"+pmsProductSaleAttrs.size());
        //2.遍历集合，查询每个pmsProductSaleAttr对应的pmsProductSaleAttrValue集合
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(productId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            System.out.println("pmsProductSaleAttrValues:"+pmsProductSaleAttrValues.size());
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }

        return pmsProductSaleAttrs;
    }

    //第二种查询sku对应的spu的销售属性和销售属性值，进行了优化，可以把isChecked查出来，前端就能定位到每个属性添加红框
    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySQL(PmsSkuInfo pmsSkuInfo) {

        //mybatis复杂查询的方式，不适用tk的方式
        List<PmsProductSaleAttr> spuSaleAttrListCheckBySQL = pmsProductSaleAttrMapper.getSpuSaleAttrListCheckBySQL(pmsSkuInfo.getSpuId(), pmsSkuInfo.getId());
        System.out.println("spuSaleAttrListCheckBySQL长度："+spuSaleAttrListCheckBySQL.size());


        return spuSaleAttrListCheckBySQL;
    }

    //获取可来回切换的sku的数据列表
    @Override
    public List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> result = pmsProductSaleAttrMapper.selectSkuSaleAttrValueListBySpu(productId);
        return result;
    }
}