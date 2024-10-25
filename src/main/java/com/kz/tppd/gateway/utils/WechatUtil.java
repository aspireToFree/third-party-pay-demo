package com.kz.tppd.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import com.kz.tppd.gateway.wechat.response.WechatErrorResponseCO;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信工具类
 * @author kz
 * @date 2024/10/7 14:16
 */
public class WechatUtil {

    /**
     * 获取微信错误返回信息CO
     * @param responseBody 返回body
     * @return 微信错误返回信息CO
     * Created by kz on 2024/10/23 15:58.
     */
    public static WechatErrorResponseCO getWechatErrorResponseCO(String responseBody){
        if(StringUtils.isBlank(responseBody)){
            return null;
        }

        try{
            return JSONObject.parseObject(responseBody , WechatErrorResponseCO.class);
        } catch (Exception e){
            //不是JSON格式错误信息
            return null;
        }
    }
}