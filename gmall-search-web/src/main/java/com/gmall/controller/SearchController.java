package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.*;
import com.gmall.service.AttrService;
import com.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * SearchController
 *
 * @Author: theliar
 * @CreateTime: 2020-03-12 / 11时 41分 55秒
 * @Description:
 */
@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttrService attrService;


    //跳转到list页面的方法，只能通过首页三级分类及首页搜素关键字跳转
    @RequestMapping("list.html")
    public String search(Map<String,Object> map, PmsSearchParam pmsSearchParam){  //使用pmsSearchParam对所有查询条件参数进行封装

        //调用searchService方法传入pmsSearchParam进行结果搜索并返回
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.search(pmsSearchParam);

        //返回list页面商品内容
        map.put("skuLsInfoList",pmsSearchSkuInfos); //list页面商品内容的显示

        //返回pmsSearchSkuInfos对应的所有平台属性和平台属性值。list页面的平台属性的展示
        //因为有的pmsSearchSkuInfos的平台属性值是同一个，就是是重复的，需要去重
        if(pmsSearchSkuInfos.size()==0){
            return null;
        }
        Set<String> valueIdsSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSearchSkuInfo.getSkuAttrValueList()) {
                valueIdsSet.add(pmsSkuAttrValue.getValueId());
            }
        }

        //调用方法返回平台属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfos =  attrService.queryBaseAttrInfoByAttrValueId(valueIdsSet);
        System.out.println("未移除对应的平台属性有:"+pmsBaseAttrInfos.size());



        //生成当前页面的url，返回给前端平台属性使用
        String urlParam = getCurrUrlParam(pmsSearchParam);
        map.put("urlParam",urlParam);

        //问题1：解决页面点击某个属性值后对应的整行属性需要去掉。不能再次点击（属性过滤后去除）
        //问题2：解决面包屑的valueName的问题。（面包屑的生成）

        //面包屑集合
        List<PmsSearchCrumb> pmsSearchCrumbs  = new ArrayList<>();

        if(pmsSearchParam.getValueId()!=null && pmsSearchParam.getValueId().length>0){
            //将对应的属性从pmsBaseAttrInfos中去除
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while(iterator.hasNext()){
                PmsBaseAttrInfo next = iterator.next();
                List<PmsBaseAttrValue> attrValueList = next.getAttrValueList();
                psmBaseAttrValue:
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    for (String s : pmsSearchParam.getValueId()) {

                        //控制属性的删除
                        if(s.equals(pmsBaseAttrValue.getId())){
                            //控制面包屑的添加
                            PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                            String crumbUrlParam = getCrumbUrlParam(pmsSearchParam, s);
                            pmsSearchCrumb.setUrlParam(crumbUrlParam);
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            pmsSearchCrumb.setValueId(s);
                            pmsSearchCrumbs.add(pmsSearchCrumb);

                            //根据平台属性值id移除对应的平台属性
                            iterator.remove();
                            continue psmBaseAttrValue;
                        }
                    }
                }
            }
        }

        System.out.println("移除后对应的平台属性有:"+pmsBaseAttrInfos.size());
        //平台属性的显示
        map.put("attrList",pmsBaseAttrInfos);




        //页面面包屑的处理  每个面包屑的url保存上一个面包屑的url 但需要去除当前跳转的url选中的平台属性

        //与过滤写在一起

//        List<PmsSearchCrumb> pmsSearchCrumbs  = new ArrayList<>();
//        if(pmsSearchParam.getValueId()!=null && pmsSearchParam.getValueId().length > 0){
//            //说明点击了某个平台属性值
//            for (String valueId : pmsSearchParam.getValueId()) {
//                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
//                pmsSearchCrumb.setValueId(valueId);
//                pmsSearchCrumb.setValueName(valueId);
//
//                //获取面包屑的属性
//                String crumbUrlParam = getCrumbUrlParam(pmsSearchParam,valueId);
//                pmsSearchCrumb.setUrlParam(crumbUrlParam);
//                pmsSearchCrumbs.add(pmsSearchCrumb);
//            }
//        }
        //向作用域放至面包屑的内容
        map.put("attrValueSelectedList",pmsSearchCrumbs);


        //返回关键字
        if(StringUtils.isNotBlank(pmsSearchParam.getKeyword())){
            map.put("keyword",pmsSearchParam.getKeyword());
        }

        return "list";
    }


    //获取每个面包屑的urlParam,不能包括当前的valueId，因为前端点x关闭后要执行其他条件的url
    public String getCrumbUrlParam(PmsSearchParam pmsSearchParam,String comeInvalueId){
        //三级id
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();

        String crumbUrlParam = "";

        if(StringUtils.isNotBlank(catalog3Id)){
            crumbUrlParam += "&" + "catalog3Id=" + catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            crumbUrlParam += "&" + "keyword=" + keyword;
        }

        if(valueId!=null && valueId.length>0){
            for (String s : valueId) {
                if(!s.equals(comeInvalueId)){
                    crumbUrlParam += "&" + "valueId=" + s;
                }
            }
        }

        if(crumbUrlParam.length()>0){
            crumbUrlParam = crumbUrlParam.substring(1);
        }

        System.out.println("urlParam:"+crumbUrlParam);

        return crumbUrlParam;
    }



    //拼接平台属性的参数urlParam
    public String getCurrUrlParam(PmsSearchParam pmsSearchParam){
        //三级id
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();

        String urlParam = "";

        if(StringUtils.isNotBlank(catalog3Id)){
            urlParam += "&" + "catalog3Id=" + catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            urlParam += "&" + "keyword=" + keyword;
        }

        if(valueId!=null && valueId.length>0){
            for (String s : valueId) {
                urlParam += "&" + "valueId=" + s;
            }
        }

        if(urlParam.length()>0){
            urlParam = urlParam.substring(1);
        }

        System.out.println("urlParam:"+urlParam);

        return urlParam;

    }

//    @RequestMapping("list")
//    public String list(){
//        return "list";
//    }


    @RequestMapping("index")
    public String index(){
        return "index";
    }

}