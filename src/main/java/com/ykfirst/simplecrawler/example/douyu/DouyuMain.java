package com.ykfirst.simplecrawler.example.douyu;

import com.ykfirst.simplecrawler.web.SimpleCrawler;

/**
 * Created by yankang on 2017/3/21.
 */
public class DouyuMain {

    public static void main(String[] args) {
        SimpleCrawler simpleCrawler = new SimpleCrawler(20, "com.ykfirst.simplecrawler.example.douyu");
        simpleCrawler.start();
    }
}
