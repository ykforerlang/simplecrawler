package com.ykfirst.simplecrawler.example.lagou;

import com.ykfirst.simplecrawler.web.SimpleCrawler;

/**
 * Created by baoz on 2017/3/21.
 */
public class LagouMain {
    private final static String DEFAULT_COOKIE = "user_trace_token=20170324081009-a3c3dbb9d0e343ce846820f306503f25; LGUID=20170324081009-3e49642a-1026-11e7-9d62-525400f775ce; index_location_city=%E5%85%A8%E5%9B%BD; _ga=GA1.2.1394320229.1490314215; Hm_lvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1490314215,1490538335; JSESSIONID=84BC46D92B5ED91A9F023B7A3FEEE294";

    public static void main(String[] args) {
        SimpleCrawler simpleCrawler = new SimpleCrawler(DEFAULT_COOKIE, 1, "com.ykfirst.simplecrawler.example.lagou");
        simpleCrawler.start();
    }
}

