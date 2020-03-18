package com.gmall.bean;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @param
 * @return
 */

public class PmsProductInfo implements Serializable {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主键自动生长策略。保存成功后自动返回主键id
    private String id;

    @Column
    private String productName;

    @Column
    private String description;

    @Column
    private  String catalog3Id;

    //前端展示的字段，需要设置为游离状态，因为不跟数据库打交道
    @Transient
    private String spuName;

    @Transient
    private List<PmsProductSaleAttr> spuSaleAttrList;

    @Transient
    private List<PmsProductImage> spuImageList;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        //用于数据库查询出来后返回前端显示
        if(spuName == null || "".equals(spuName)){
            spuName = productName;
        }
    }

    public List<PmsProductSaleAttr> getSpuSaleAttrList() {
        return spuSaleAttrList;
    }

    public void setSpuSaleAttrList(List<PmsProductSaleAttr> spuSaleAttrList) {
        this.spuSaleAttrList = spuSaleAttrList;
    }

    public List<PmsProductImage> getSpuImageList() {
        return spuImageList;
    }

    public void setSpuImageList(List<PmsProductImage> spuImageList) {
        this.spuImageList = spuImageList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
        //赋值给productName
        if(productName == null || "".equals(productName)){
            productName = spuName;
        }
    }
}


