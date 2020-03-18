package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.OmsCartItem;
import com.gmall.mapper.CartItemMapper;
import com.gmall.service.CartService;
import com.gmall.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * CartServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-15 / 20时 33分 14秒
 * @Description:
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Override
    public OmsCartItem findOmsCartItemByMemberIdAndSkuId(String memberId, String SkuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(SkuId);
        OmsCartItem omsCartItem1 = cartItemMapper.selectOne(omsCartItem);
        return omsCartItem1;
    }

    @Override
    public void insert(OmsCartItem omsCartItem) {
        //添加一个购物车
        cartItemMapper.insertSelective(omsCartItem);

        //这里对redis进行同步   key:User:memberId:cart  value hash集合（key：skuId，value：OmsCartItem）
        Jedis jedis = redisUtil.getJedis();
        jedis.hset("User:"+omsCartItem.getMemberId()+":cart",omsCartItem.getProductSkuId(),JSON.toJSONString(omsCartItem));
        jedis.close();
    }

    @Override
    public void update(OmsCartItem omsCartItemDB) {

        //更新时设置set后面的变化值(目前只更新数量)
        OmsCartItem omsCartItem = new OmsCartItem();
        //更新数量
        omsCartItem.setQuantity(omsCartItemDB.getQuantity());
        //更新选中状态
        omsCartItem.setIsChecked(omsCartItemDB.getIsChecked());

        //更新时设置where后面的查询条件
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId",omsCartItemDB.getProductSkuId()).andEqualTo("memberId",omsCartItemDB.getMemberId());

        cartItemMapper.updateByExampleSelective(omsCartItem,example);

        //这里对redis进行同步   key:User:memberId:cart  value hash集合（key：skuId，value：OmsCartItem）
        Jedis jedis = redisUtil.getJedis();
        jedis.hset("User:"+omsCartItemDB.getMemberId()+":cart",omsCartItemDB.getProductSkuId(),JSON.toJSONString(omsCartItemDB));
        jedis.close();

    }

    @Override
    public List<OmsCartItem> findOmsCartItemListByMemberId(String memberId) {

        List<OmsCartItem> result = new ArrayList<>();

        //先走缓存
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("User:" + memberId + ":cart");

        if(hvals!=null && hvals.size()>0){
            //缓存有数据
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                result.add(omsCartItem);
            }
        }else{
            //缓存无数据 走db
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(memberId);
            result = cartItemMapper.select(omsCartItem);
            //同步到缓存
            if(result!=null){
                for (OmsCartItem cartItem : result) {
                    jedis.hset("User:"+memberId+":cart",cartItem.getProductSkuId(),JSON.toJSONString(cartItem));
                }
            }
            jedis.close();
        }

        return result;
    }

}