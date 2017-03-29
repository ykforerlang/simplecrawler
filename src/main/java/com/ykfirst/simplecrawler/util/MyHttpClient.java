package com.ykfirst.simplecrawler.util;

import com.ykfirst.simplecrawler.web.SiteInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by baoz on 2017/3/24.
 */
public class MyHttpClient {
    private final CloseableHttpClient httpclient;

    public MyHttpClient() {
        this(60000, 60000, "");
    }

    public MyHttpClient(String defaultCookie) {
        this(60000, 60000, defaultCookie);
    }

    public MyHttpClient(int connectionTimeout, int transportTimeout, String defaultCookie) {
        this(20, 20, connectionTimeout, transportTimeout, defaultCookie);
    }

    public MyHttpClient(int poolSize, int perRouteSize, int connectionTimeout,
                        int transportTimeout, String defaultCookie) {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(poolSize);
        connManager.setDefaultMaxPerRoute(perRouteSize);
        // connManager.setMaxPerRoute(route, max);

        RequestConfig.Builder custom = RequestConfig.custom();
        custom.setConnectTimeout(connectionTimeout);
        //custom.setConnectionRequestTimeout(0);// not limit
        custom.setSocketTimeout(transportTimeout);
        // custom.set


        RequestConfig config = custom.build();



        Collection<? extends Header> defaultHeaders = getDefaultHeaders(defaultCookie);
        httpclient = HttpClients.custom().setConnectionManager(connManager)
                .setDefaultRequestConfig(config)
                .setDefaultHeaders(defaultHeaders).build();
    }


    private Collection<? extends Header> getDefaultHeaders(String defaultCookie) {
        List<Header> lists = new ArrayList<Header>();
        lists.add(new BasicHeader("Accept-Language",
                "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
        lists.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; rv:39.0) Gecko/20100101 Firefox/39.0"));

        lists.add(new BasicHeader("Cookie", defaultCookie));
        return lists;
    }


    /**
     * 保存静态资源
     *
     * @param url
     * @param file
     * @throws ClientProtocolException
     * @throws IOException
     */
    public DownLoadState storeStaticResource(String url, String file)
            throws ClientProtocolException, IOException {
        HttpEntity entity = null;
        FileOutputStream out = null;
        InputStream content = null;
        try {
            File f = new File(file);
            if (f.exists())
                return DownLoadState.FILE_EXISTS;

            HttpGet get = new HttpGet(url);

            CloseableHttpResponse execute = httpclient.execute(get);
            entity = execute.getEntity();
            content = entity.getContent();
            StatusLine statusLine = execute.getStatusLine();
            // 如果请求错误，返回, 不创建文件
            if (statusLine.getStatusCode() != 200
                    || entity.getContentLength() == 0)
                return DownLoadState.DOWN_ERROR;
            f.getParentFile().mkdirs();
            f.createNewFile();
            out = new FileOutputStream(f);

            byte bytes[] = new byte[1000];
            int read = 0;
            while ((read = content.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
			/* get.abort(); */
            return DownLoadState.DOWN_SUCCESS;

        } finally {
            if (out != null) {
                out.close();
            }
            if (content != null) {
                content.close();
            }
            // EntityUtils.consume(entity);
        }
    }
    public static enum DownLoadState {
        DOWN_SUCCESS, DOWN_ERROR, FILE_EXISTS
    }


    /**
     * 以支付串的形式获取网址的内容
     *
     * @param site
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String getStringResponse(SiteInfo site) {
        return getStringResponse(site, "utf-8");
    }
    public String getStringResponse(SiteInfo site, String format) {
        CloseableHttpResponse res = null;
        HttpEntity entity = null;
        try {
            res = this.getStringResponseInternal(site, format);
            entity = res.getEntity();
            return EntityUtils.toString(entity, format);
        } catch (Exception e) {
            throw new RuntimeException(site + "to string error !", e);
        } finally {
            if (res != null) {
                try {
                    InputStream content = entity.getContent();
                    if (content != null)
                        content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CloseableHttpResponse getStringResponseInternal(SiteInfo site,
                                                            String format) throws ClientProtocolException, IOException {
        if (site == null)
            return null;

        CloseableHttpResponse res = null;
        HttpGet get = null;
        HttpPost post = null;
        if (site.methodType.equals(SiteInfo.GET)) {
            get = new HttpGet(site.url);

            res = httpclient.execute(get);
        } else {
            // 其他默认为post
            String context = site.getParamsString();
            post = new HttpPost(site.url);
            StringEntity stringEntity = new StringEntity(context);
            stringEntity.setContentType("application/x-www-form-urlencoded");
            post.setEntity(stringEntity);
            res = httpclient.execute(post);
        }
        return res;
    }
}
