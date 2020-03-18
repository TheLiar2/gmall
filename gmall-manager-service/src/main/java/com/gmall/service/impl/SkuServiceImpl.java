package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.bean.PmsSkuAttrValue;
import com.gmall.bean.PmsSkuImage;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.bean.PmsSkuSaleAttrValue;
import com.gmall.mapper.PmsSkuAttrValueMapper;
import com.gmall.mapper.PmsSkuImageMapper;
import com.gmall.mapper.PmsSkuInfoMapper;
import com.gmall.mapper.PmsSkuSaleAttrValueMapper;
import com.gmall.service.SkuService;
import com.gmall.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * SkuServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-05 / 19时 59分 55秒
 * @Description:
 */
@Service
public class SkuServiceImpl implements SkuService {

    //sku
    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    //sku image
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    //sku attr_value
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    //sku sale_attr_value
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;


    //引入redisUtil
    @Autowired
    private RedisUtil redisUtil;

    //保存一个sku
    @Override    //前端除了sku_id没封装，其他已经封装好直接insert即可
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //1.保存sku到pms_sku_info
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();
        //2.保存图片到sku_image
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        //3.保存属性关系到pms_sku_attr_value
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //4.保存销售属性值到pms_sku_sale_attr_value
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
    }

    //通过skuId查询一个sku信息
    @Override
    public PmsSkuInfo getSkuInfoBySkuId(String skuId) {
        //1.先查询对应的sku
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        //从redis缓存中取数据
        Jedis jedis = null;
        try {
            //获取jedis连接
            jedis = redisUtil.getJedis();
            String skuInfoByRedis = jedis.get("sku:" + skuId + ":info");

            if(skuInfoByRedis == null || skuInfoByRedis.length() == 0){
                System.out.println("走db");
                //使用redis分布式锁nx。
                //1.设置分布式锁时间，防止操作数据库时间过长，占用锁时间长。
                //每次线程生成一个uuid，防止某个线程执行时间长，过期时间key失效后，删除别人的key。
                String uuid = UUID.randomUUID().toString();
                String OK = jedis.set("redis:" + skuId + ":lock", uuid, "nx", "px", 10000);
                if(!StringUtils.isEmpty(OK) && "OK".equals(OK)){
                    System.out.println("当前线程获得分布式锁");
                    //当前线程获得锁后才做数据库
                    //缓存无数据，查db
                    pmsSkuInfo.setId(skuId);
                    pmsSkuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
                    //放入redis缓存
                    jedis.set("sku:"+skuId+":info", JSON.toJSONString(pmsSkuInfo));

                    //释放分布式锁
                    //第一种，判断后是否属于自己当前线程的锁（不安全）。判断期间也有可能存在刚好key过期。误删其他线程的锁
                    String lockValue = jedis.get("redis:" + skuId + ":lock");
                    if(!StringUtils.isEmpty(lockValue) && uuid.equals(lockValue)){
                        //删除key
                        jedis.del("redis:"+skuId+":lock");
                    }
                    //第二种使用lua语言，因为底层是c语言，判断后立刻删除。时间极快
                    String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("redis:" + skuId + ":lock"),Collections.singletonList(uuid));
                }else{
                    //无法获得redis分布式锁，多个线程仍然等待获得分布式锁，使用自旋解决。再调用可从缓存取
                    return getSkuInfoBySkuId(skuId);
                }
            }else{
                System.out.println("走缓存");
                //缓存有数据   转化为对象
                pmsSkuInfo = JSONObject.parseObject(skuInfoByRedis,PmsSkuInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关系jedis客户端连接
            jedis.close();
        }


        //2.查询sku对应的saleAttrValue
        PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
        pmsSkuSaleAttrValue.setSkuId(skuId);
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
        pmsSkuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);

        //3.查询sku对应的image
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo.setSkuImageList(pmsSkuImageList);
        return pmsSkuInfo;
    }


    //用于es查到所有的pmsskuInfo，与pmsSearchSkuInfo作转换
    @Override
    public List<PmsSkuInfo> findAll() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        //将PmsSkuInfo对应的pms_sku_attr_value表查出来，用于同于平台属性或平台属性值id查到到该商品
        //pms_sku_sale_attr_value并没有查
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValueList);
        }
        return pmsSkuInfos;
    }
}