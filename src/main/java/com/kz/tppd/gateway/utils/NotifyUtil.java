package com.kz.tppd.gateway.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 回调工具类
 * @author kz
 * @date 2019/2/14 14:44.
 */
@Slf4j
public class NotifyUtil {

    /**
    * response写数据并返回
    * @param response http response
    * @param msg 返回内容
    * Created by kz on 2019/2/14 14:45.
    */
    public static void returnPrintWriter(HttpServletResponse response , String msg) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(msg);
        // 释放资源
        out.flush();
    }

    /**
     * 获取post请求中的Body
     * @param request httpRequest
     * @return body字符串
     */
    public static String getBodyString(ServletRequest request , String charSet) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            //读取流并将流写出去,避免数据流中断;
            reader = new BufferedReader(new InputStreamReader(inputStream, charSet));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("获取requestBody异常" , e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(reader);
        }
        return sb.toString();
    }
}