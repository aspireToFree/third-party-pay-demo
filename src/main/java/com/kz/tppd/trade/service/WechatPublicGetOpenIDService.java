package com.kz.tppd.trade.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * 微信公众号获取 OpenID service
 * @author kz
 * @date 2024/12/25 11:10
 */
@Slf4j
@Service
public class WechatPublicGetOpenIDService {

    /**
     * 获取code重定向地址
     * @return 地址
     * Created by kz on 2023/6/12 0:03.
     */
    public String getCode(HttpServletRequest request) throws UnsupportedEncodingException {
        //校验是不是微信浏览器打开
        String payMethod = getPayMethod(request);
        if(payMethod == null){
            throw new RuntimeException("请用微信");
        }

        String wechatPublicAppid = "微信公众号appId";

        //state 用户信息（建议加密），回传时用到
        String state = "";

        //getOpenIdUrl 自己的获取openId公网地址(获取code成功后，会重定向回该地址))
        String getOpeniIdUrl = "https://自己的网页授权域名/wechat/getOpenId";
        return oauth2buildAuthorizationUrl(wechatPublicAppid , getOpeniIdUrl, state);
    }

    /**
     * 构建认证url
     * @param appId appId
     * @param redirectURI 重定向地址
     * @param state 用户信息，回传时用到
     * @return 地址
     */
    private String oauth2buildAuthorizationUrl(String appId, String redirectURI, String state) throws UnsupportedEncodingException {
        return String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect",
                appId, URLEncoder.encode(redirectURI , "UTF-8"),  StringUtils.trimToEmpty(state));
    }

    /**
     * 获取支付方式
     * @param request http request
     * @return 支付方式
     */
    private String getPayMethod(HttpServletRequest request){
        String userAgent = null;
        Enumeration<String> em = request.getHeaderNames();
        while (em.hasMoreElements()) {
            String name = em.nextElement();
            String value = request.getHeader(name);
            if("user-agent".equals(name)){
                userAgent = value;
                break;
            }
        }
        if(StringUtils.isBlank(userAgent)){
            return null;
        }

        if(userAgent.contains("MicroMessenger")){
            return "WECHAT";
        } else if(userAgent.contains("AlipayClient")){
            return "ALIPAY";
        }
        log.info("userAgent:{}" , userAgent);
        return null;
    }

    /**
     * 静默授权-微信code
     * @param code 微信code
     * @param state url中传的加密参数
     * @return 地址
     * Created by kz on 2023/6/12 0:03.
     */
    public String wechatCode(String code, String state){
        //获取微信openId
        String openId = getWechatOpenId(code);

        //state为，获取code时，自己设置的附加参数
        return "前端获取OpenID后的成功页面";
    }

    /**
     * 获取微信openId
     * @param code 微信code
     * @return 微信openId
     * Created by kz on 2023/9/13 22:12.
     */
    private String getWechatOpenId(String code){
        String wechatPublicAppid = "微信公众号appId";
        String wechatPublicAppSecret = "微信公众号开发密钥";

        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code"
                , wechatPublicAppid , wechatPublicAppSecret , code);
        log.info("微信获取openId 请求地址:{}" , url);
        String httpReponse = HttpUtil.get(url);
        log.info("微信获取openId 返回参数:{}" , httpReponse);
        if(StringUtils.isBlank(httpReponse)){
            throw new RuntimeException("微信获取openId返回参数为空");
        }

        JSONObject jsonObject = JSON.parseObject(httpReponse);
        //返回失败
        if(jsonObject.containsKey("errcode")){
            throw new BaseException(CommonErrorEnum.BUSINESS_ERROR.getCode() , jsonObject.getLong("errcode") + "：" + jsonObject.getString("errmsg"));
        }

        String openId = jsonObject.getString("openid");
        if(StringUtils.isBlank(openId)){
            throw new BaseException(CommonErrorEnum.BUSINESS_ERROR.getCode() , "微信返回的openId为空");
        }
        return openId;
    }
}