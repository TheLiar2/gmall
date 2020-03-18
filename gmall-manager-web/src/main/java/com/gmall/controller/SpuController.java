package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsBaseSaleAttr;
import com.gmall.bean.PmsProductImage;
import com.gmall.bean.PmsProductInfo;
import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.service.SpuService;
import com.gmall.utils.FastDFSUtils;
import org.csource.common.MyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * SpuController
 * spu商品 -> 销售属性 -> 销售属性值   这些属性由商家自行制定
 *      一对多      一对多
 * @Author: theliar
 * @CreateTime: 2020-03-03 / 22时 50分 31秒
 * @Description:
 */
@RestController
@CrossOrigin
public class SpuController {


    @Reference
    private SpuService spuService;

    /**
     * //通过spuId查询spu对应的product_sale_attr(包络product_sale_attr_value)
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(@RequestParam("spuId")Integer spuId){
        List<PmsProductSaleAttr> result = spuService.spuSaleAttrListBySpuId(spuId);
        return result;
    }

    /**
     * 通过spuId查询spu对应的product_image
     * @param spuId
     * @return
     */
    @RequestMapping("spuImageList")
    public List<PmsProductImage> spuImageList(@RequestParam("spuId")Integer spuId){
        List<PmsProductImage> result = spuService.spuImageListBySpuId(spuId);
        return result;
    }



    /**
     * 通过三级id查询三级id下的所有spu商品（不包括product_image。product_sale_attr,product_sale_attr_value）
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(@RequestParam("catalog3Id")Integer catalog3Id){
        //调用spuService
        List<PmsProductInfo> result =spuService.spuListByCatalog3Id(catalog3Id);
        return result;
    }


    /**
     * 保存spu的信息
     * @param pmsProductInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody  PmsProductInfo pmsProductInfo){
        //默认情况下前端为spuName，但实体是productName。为实体添加spuName。在set的时候赋值给productName
        //调用spuService保存pmsProductInfo实例
        spuService.savePmsProductInfo(pmsProductInfo);
        System.out.println("leave...");
        return "success";
    }


    /**
     * 查询pms_base_sale_attr列表（及spu基本销售属性列表4个）,用于前台下拉选
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        //调用spuService查询spu基本销售属性列表
        List<PmsBaseSaleAttr> result = spuService.getBaseSaleAttrList();

        for (PmsBaseSaleAttr pmsBaseSaleAttr : result) {
            System.out.println(pmsBaseSaleAttr);
        }
        return result;
    }



    /**
     * 新建spu将图片上传到fastdfs中
     */
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        System.out.println("fileUpload ... ");
        String filePath = FastDFSUtils.upload(file);
        return filePath;
    }


}