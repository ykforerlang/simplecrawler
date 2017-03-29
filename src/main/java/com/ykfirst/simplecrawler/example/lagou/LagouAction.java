package com.ykfirst.simplecrawler.example.lagou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ykfirst.simplecrawler.core.Task;
import com.ykfirst.simplecrawler.core.anno.Action;
import com.ykfirst.simplecrawler.core.anno.Init;
import com.ykfirst.simplecrawler.core.anno.TaskSolving;
import com.ykfirst.simplecrawler.core.anno.TaskSubmiting;
import com.ykfirst.simplecrawler.web.ResJsonTask;
import com.ykfirst.simplecrawler.web.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baoz on 2017/3/24.
 */
@Action
public class LagouAction {
    private static  final Logger log = LoggerFactory.getLogger(LagouAction.class);

    private static final String  NanJingUrlBase = "https://www.lagou.com/jobs/positionAjax.json?px=new&city=%E5%8D%97%E4%BA%AC&needAddtionalResult=false";
    private static final String LAGOU_INIT =  "LAGOU_INIT";
    private static final String LAGOU_PAGE =  "LAGOU_PAGE";

    @Init
    @TaskSubmiting
    public Task<SiteInfo> init() {
        Map<String, String> params = new HashMap<>();
        params.put("first", "false");
        params.put("pn", "1");

        SiteInfo si = new SiteInfo(NanJingUrlBase);
        return new ResJsonTask(LAGOU_INIT, si);
    }

    @TaskSolving(LAGOU_INIT)
    @TaskSubmiting
    public List<ResJsonTask> initSolving(JSONObject resJson) {
        log.info(LAGOU_INIT  + resJson);
        JSONObject positionResult = resJson.getJSONObject("content").getJSONObject("positionResult");
        int pageSize = resJson.getJSONObject("content").getIntValue("pageSize");
        int total = positionResult.getIntValue("totalCount");

        int pageCount = total % pageSize == 0 ? total / pageSize : total /pageSize + 1;

        List<ResJsonTask> result = new ArrayList<>();
        for(int i = 2; i <= pageCount;i ++) {
            Map<String, String> params = new HashMap<>();
            params.put("first", "false");
            params.put("pn", i + "");
            result.add(new ResJsonTask(LAGOU_PAGE, new SiteInfo(NanJingUrlBase, params)));
        }

        return result;
    }

    @TaskSolving(LAGOU_PAGE)
    public void lagouPage(JSONObject resJson) {
        log.info(LAGOU_PAGE  + resJson);
    }

}
