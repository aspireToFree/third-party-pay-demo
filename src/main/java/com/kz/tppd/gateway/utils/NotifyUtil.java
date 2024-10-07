package com.kz.tppd.gateway.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 回调工具类
 * @author kz
 * @date 2019/2/14 14:44.
 */
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
}