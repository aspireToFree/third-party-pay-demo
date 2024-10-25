package com.kz.tppd.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;

/**
 * http工具类
 * Created by kz on 2017/12/26.
 */
public class HttpUtils {

    /** 从连接池中获取连接的超时时间 */
    private static final int CONNEC_REQUEST_TIME_OUT = 10000;

    /** http连接超时时间 */
    private static final int CONNEC_TIME_OUT = 30000;

    /** 套接字超时时间 */
    private static final int SOCKET_TIME_OUT = 30000;

    /**
     * 发送httppost表单请求
     * @param request 请求字符串
     * @param url 请求url
     * @param charset 字符集
     * @param contentType 请求类型
     * @return 响应字符串
     */
    public static String post(String request, String url, String charset, String contentType) {
        return post(request , url , charset , null ,contentType , null , null , null);
    }

    /**
     * 发送httppost表单请求
     * @param request 请求字符串
     * @param url 请求url
     * @param charset 字符集
     * @param acceptType 接收类型
     * @param contentType 请求类型
     * @param httpHeaderMap 请求头 map集
     * @param keyStore 证书存储对象
     * @param keyPassWord 证书密码
     * @return 响应字符串
     */
    public static String post(String request, String url, String charset, String acceptType, String contentType, Map<String, String> httpHeaderMap , KeyStore keyStore , String keyPassWord) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);
            if(StringUtils.isNotBlank(acceptType)){
                httpPost.setHeader("Accept" , acceptType);
            }
            StringEntity httpEntity = new StringEntity(request, charset);
            httpEntity.setContentType(contentType);
            httpPost.setEntity(httpEntity);
            if(httpHeaderMap != null){
                for(String headKey : httpHeaderMap.keySet()){
                    httpPost.setHeader(headKey , httpHeaderMap.get(headKey));
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNEC_TIME_OUT).setConnectionRequestTimeout(CONNEC_REQUEST_TIME_OUT)
                    .setSocketTimeout(SOCKET_TIME_OUT).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpClient.execute(httpPost);
            return EntityUtils.toString(httpResponse.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException("请求异常", e);
        } finally {
            if(httpResponse != null){
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
