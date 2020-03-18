package com.gmall.service;

import com.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * CartService
 *
 * @Author: theliar
 * @CreateTime: 2020-03-15 / 20时 32分 44秒
 * @Description:
 */
public interface CartService {

    //通过用户id和skuId判断该用户是否已经添加过该商品到购物车
    OmsCartItem findOmsCartItemByMemberIdAndSkuId(String memberId, String SkuId);

    //插入一条OmsCartItem
    void insert(OmsCartItem omsCartItem);

    //更新一条OmsCartItem，数量的更新和选中的状态更新
    void update(OmsCartItem omsCartItemDB);

    //先从缓存查没有到db
    List<OmsCartItem> findOmsCartItemListByMemberId(String memberId);


}
