package com.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.bean.PmsSearchParam;
import com.gmall.bean.PmsSearchSkuInfo;
import com.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SearchServiceImpl
 *
 * @Author: theliar
 * @CreateTime: 2020-03-12 / 16时 44分 00秒
 * @Description:
 */
@Service
public class SearchServiceImpl implements SearchService {

    //es的mapper
    @Autowired
    private JestClient jestClient;

    //根据条件查询es中的数据
    @Override
    public List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) {
        //1.查询参数的封装  使用一个方法返回
        String esQL = esDSL(pmsSearchParam);
        Search build = new Search.Builder(esQL).addIndex("gmall1015").addType("pmsSearchSkuInfo").build();
        try {
            //对es进行搜索
            SearchResult execute = jestClient.execute(build);
            System.out.println("查询结果total："+execute.getTotal());

            //2.结果集的处理与封装
            List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
            //获取结果中的hits数组
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            //遍历hits数组中每个元素
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                //获取_source属性对应的对象
                PmsSearchSkuInfo source = hit.source;
                //解析和替换高亮
                Map<String, List<String>> highlight = hit.highlight;

                if(highlight!=null && highlight.size()>0){
                    String skuName = highlight.get("skuName").get(0);
                    source.setSkuName(skuName);
                }
                pmsSearchSkuInfos.add(source);
            }
            System.out.println("处理完结果集的个数有："+pmsSearchSkuInfos.size());
            return pmsSearchSkuInfos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String esDSL(PmsSearchParam pmsSearchParam){
        //es中查询条件的封装对象 {}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();

        //设置结果的起始位置和总数量
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);

        //{"query":{"bool":{} }}  bool对象
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //三级分类id    query->bool->filter->term
        if(StringUtils.isNoneBlank(catalog3Id)){
//            {
//                "query": {
//                "bool": {
//                    "filter": {
//                        "term": {
//                            "catalog3Id": "61"
//                        }
//                    }
//                }
//            },
//                "from": 0, "size": 50
//            }
            System.out.println("三级分类："+catalog3Id);
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }


        //关键字
        if(StringUtils.isNoneBlank(keyword)){
            System.out.println("关键字："+keyword);
            //match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            //much
            boolQueryBuilder.must(matchQueryBuilder);

            //设置关键字高亮   加粗，红色
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");
            //关键字前缀
            highlightBuilder.preTags("<span style='font-weight:bold;color:red'>");
            //关键字后缀
            highlightBuilder.postTags("</span>");
            //将高亮放入query中
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //平台属性
        if(valueId!=null && valueId.length>0){
            for (String itemValueId : valueId) {
                //添加es查询条件去
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",itemValueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }



        //将bool放去query中
        searchSourceBuilder.query(boolQueryBuilder);

        return searchSourceBuilder.toString();
    }
}