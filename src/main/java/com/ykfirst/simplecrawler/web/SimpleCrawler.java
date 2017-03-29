package com.ykfirst.simplecrawler.web;

import com.alibaba.fastjson.JSON;
import com.ykfirst.simplecrawler.core.*;
import com.ykfirst.simplecrawler.core.anno.Action;
import com.ykfirst.simplecrawler.core.anno.Init;
import com.ykfirst.simplecrawler.core.anno.TaskSolving;
import com.ykfirst.simplecrawler.util.MyHttpClient;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by baoz on 2017/3/21.
 */
public class SimpleCrawler extends TaskExecutor<SiteInfo>{
    private final Logger log = LoggerFactory.getLogger(SimpleCrawler.class);
    private final MyHttpClient hc;



    public SimpleCrawler(int poolSize, String... packages){
        super(poolSize, packages);
        hc = new MyHttpClient();
    }

    public SimpleCrawler(String defaultCookie, int poolSize, String... packages){
        super(poolSize, packages);
        hc = new MyHttpClient(defaultCookie);
    }

    public SimpleCrawler(ThreadPoolExecutor es, String... packages){
        super(es, packages);
        hc = new MyHttpClient();
    }

    public SimpleCrawler(String defaultCookie, ThreadPoolExecutor es, String... packages){
        super(es, packages);
        hc = new MyHttpClient(defaultCookie);
    }

    @Override
    protected Runnable geneRunnableFromTask(final  Task<SiteInfo> task) {
        if(task instanceof ResJsonTask) {
            return new Runnable() {
                @Override
                public void run() {
                    log.info("execute task: {}", task.toString());
                    RunInfo ri = getRunInfo(task.getType());
                    if (ri == null) return;
                    try {
                        SiteInfo date = task.getDate();
                        JSON.parse(hc.getStringResponse(date));
                        ri.execute(JSON.parse(hc.getStringResponse(date)));
                    } catch (Exception e) {
                        log.info("handle {}, {}, {}", task.toString(), " error", e.getMessage());
                    }
                }
            };
        }

        if(task instanceof ResHtmlTask) {
            return new Runnable() {
                @Override
                public void run() {
                    RunInfo ri = getRunInfo(task.getType());
                    if (ri == null) return;
                    try {
                        SiteInfo date = task.getDate();
                        ri.execute(hc.getStringResponse(date));
                    } catch (Exception e) {
                        log.info("handle {}, {}", task.toString(), " error");
                        e.printStackTrace();
                    }
                }
            };
        }

        if(task instanceof StaticResourceTask) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        hc.storeStaticResource(((StaticResourceTask) task).getUrl(), ((StaticResourceTask) task).getFilePath());
                    } catch (Exception e) {
                        log.info("handle {}, {}", task.toString(), " error");
                        e.printStackTrace();
                    }
                }
            };
        }


        return super.geneRunnableFromTask(task);
    }
}
