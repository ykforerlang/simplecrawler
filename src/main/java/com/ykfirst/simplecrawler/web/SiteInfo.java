package com.ykfirst.simplecrawler.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baoz on 2017/3/23.
 */
public class SiteInfo {
    public static final String POST = "POST";
    public static final String GET = "GET";

    public final String methodType;
    public final String url;
    public final Map<String, String> params;

    private final String paramsString;

    public SiteInfo(String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
        this.methodType = POST;

        this.paramsString = geneParamString(params);
    }

    public SiteInfo(String url) {
        this.url = url;
        this.params = new HashMap<>();
        this.methodType = GET;

        this.paramsString = "";
    }

    public SiteInfo(String methodType, String url, Map<String, String> params){
        this.methodType = methodType;
        this.url = url;
        this.params = params;

        this.paramsString = geneParamString(params);
    }

    private String geneParamString(Map<String, String> params){
        String result = "";
        for(String key : params.keySet()) {
            String value = params.get(key);
            result += ("&" + key + "=" + value);
        }
        return result.substring(1);
    }

    public String getParamsString() {
        return paramsString;
    }

}
