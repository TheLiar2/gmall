package com.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.OmsCartItem;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.service.CartService;
import com.gmall.service.SkuService;
import com.gmall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CartWebController
 *
 * @Author: theliar
 * @CreateTime: 2020-03-14 / 23时 29分 09秒
 * @Description:
 */

@Controller
public class CartController {



    @Reference
    private CartService cartService;


    @Reference
    private SkuService skuService;

    //改变购物车的状态
    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,String isChecked,String productSkuId,Map<String,Object> map){

        List<OmsCartItem> omsCartItemList = null;

        String userId = "1";

        if(StringUtils.isNotBlank(userId)){
            //走db
            //更新db状态跟刷新redis状态。更新前先获取原数据，然后对源数据进行操作
            OmsCartItem omsCartItemByMemberIdAndSkuId = cartService.findOmsCartItemByMemberIdAndSkuId(userId, productSkuId);
            omsCartItemByMemberIdAndSkuId.setIsChecked(isChecked);
            cartService.update(omsCartItemByMemberIdAndSkuId);

            //获取db数据
            omsCartItemList = cartService.findOmsCartItemListByMemberId(userId);

        }else{
            //走cookie
            String cartListCookie = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItemList = JSON.parseArray(cartListCookie,OmsCartItem.class);
                for (OmsCartItem omsCartItem : omsCartItemList) {
                    if(omsCartItem.getProductSkuId().equals(productSkuId)){
                        omsCartItem.setIsChecked(isChecked);
                    }
                }
                //放回cookie
                CookieUtil.setCookie(httpServletRequest,httpServletResponse,"cartListCookie",JSON.toJSONString(omsCartItemList),60*60*24,true);
            }

        }
        //计算总价格
        if(omsCartItemList!=null){

            BigDecimal cartTotalPrice = new BigDecimal("0");

            for (OmsCartItem omsCartItem : omsCartItemList) {
                omsCartItem.setTotalPrice(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
                //计算集合中选中商品的价格
                if(omsCartItem.getIsChecked().equals("1")){
                    cartTotalPrice = cartTotalPrice.add(omsCartItem.getTotalPrice());
                }
            }

            map.put("cartTotalPrice",cartTotalPrice);
        }

        map.put("cartList",omsCartItemList);


        return "cartListInner";
    }


    //查询购物车数据
    @RequestMapping("cartList")
    public String cartList(Map<String,Object> map,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        List<OmsCartItem> omsCartItemList = null;
        String userId = "1";

        if(userId!=null && userId.length()>0){
            //走缓存 缓存没有再走db
            omsCartItemList = cartService.findOmsCartItemListByMemberId(userId);

        }else{
            //走cookie
            String cartListCookieStr = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookieStr)){
                omsCartItemList = JSON.parseArray(cartListCookieStr,OmsCartItem.class);
            }

        }

        map.put("cartList",omsCartItemList);

        //计算总价格
        if(omsCartItemList!=null){

            BigDecimal cartTotalPrice = new BigDecimal("0");

            for (OmsCartItem omsCartItem : omsCartItemList) {
                omsCartItem.setTotalPrice(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
                //合集算中商品的价格
                if(omsCartItem.getIsChecked().equals("1")){
                    cartTotalPrice = cartTotalPrice.add(omsCartItem.getTotalPrice());
                }
            }

            map.put("cartTotalPrice",cartTotalPrice);
        }
        return "cartList";
    }




    //添加购物车
    @RequestMapping("addToCart")
    public String addToCart(OmsCartItem omsCartItem, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

//        if(omsCartItem == null){
//
//        }

        //将omsCartItem其他属性进行set
        PmsSkuInfo skuInfoBySkuId = skuService.getSkuInfoBySkuId(omsCartItem.getProductSkuId());
        //spuId
        omsCartItem.setProductId(skuInfoBySkuId.getProductId());
        //价格
        omsCartItem.setPrice(skuInfoBySkuId.getPrice());
        //sku名称
        omsCartItem.setProductName(skuInfoBySkuId.getSkuName());
        //三级分类id
        omsCartItem.setProductCategoryId(skuInfoBySkuId.getCatalog3Id());
        //创建时间
        omsCartItem.setCreateDate(new Date());
        //sku默认图片
        omsCartItem.setProductPic(skuInfoBySkuId.getSkuDefaultImg());
        //默认选中
        omsCartItem.setIsChecked("1");



        System.out.println("ready to addToCart");

        //判断用户是否登录
        String userId = "1";

        //购物车集合
        List<OmsCartItem> omsCartItemList  = new ArrayList<>();

        if(userId!=null && userId.length()>0){
            System.out.println("走db");
            //走db
            OmsCartItem omsCartItemDB = cartService.findOmsCartItemByMemberIdAndSkuId(userId,omsCartItem.getProductSkuId());
            if(omsCartItemDB!=null){
                //db有数据，进行修改
                omsCartItemDB.setQuantity(omsCartItemDB.getQuantity().add(omsCartItem.getQuantity()));
                cartService.update(omsCartItemDB);
            }else{
                //db无数据，进行添加
                omsCartItem.setMemberId(userId);
                cartService.insert(omsCartItem);
            }

            //不管结果如何，刷新redis缓存(这种不建议，建议单个修改的方式好)
        }else{
            //走cookie
            System.out.println("走cookie");
            //使用cookieUtil获取cartListCookie
            String cartListCookie = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookie)){
                //说明没有该cookie，直接添加
                omsCartItemList.add(omsCartItem);
            }else{
                //有该cookie，判断是否有重复商品
                omsCartItemList = JSON.parseArray(cartListCookie,OmsCartItem.class);
                //遍历是否有相同的skuId,有数量上加1，没有添加上去
                boolean flag = false;
                for (OmsCartItem cartItem : omsCartItemList) {
                    if(omsCartItem.getProductSkuId().equals(cartItem.getProductSkuId())){
                        System.out.println("原cookie中有对应的信息，数量为:"+cartItem.getQuantity());
                        //说明cookie中有对应的商品信息，数量加1
                        cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        flag = true;
                    }
                }
                if(!flag){
                    //cookie没有对应的信息，添加上去
                    omsCartItemList.add(omsCartItem);
                }
            }
            //不管什么结果。最终将cookie返回给浏览器
            CookieUtil.setCookie(httpServletRequest,httpServletResponse,"cartListCookie",JSON.toJSONString(omsCartItemList),60*60*24,true);

        }



        //使用重定向，防止再次刷新提交
        return "redirect:/success.html";
    }


//    @RequestMapping("success")
//    public String success(){
//        return "success";
//    }


}