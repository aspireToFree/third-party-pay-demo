package com.kz.tppd.test;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import org.junit.Test;

/**
 * 微信直联测试
 * @author kz
 * @date 2024/12/19 10:34
 */
public class WechatDirectTest {

    //微信JSAPI测试
    @Test
    public void jsapi(){
        Config config = getWechatConfig();

        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(config).build();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        //金额转分
        amount.setTotal(1);
        request.setAmount(amount);
        request.setAppid("公众号或小程序appId");

        Payer payer = new Payer();
        payer.setOpenid("用户openId");
        request.setPayer(payer);

        request.setMchid("微信商户号");

        // 设置订单标题
        request.setDescription("测试");
        // 设置商户订单号
        request.setOutTradeNo(System.currentTimeMillis() + "");

        String label = "微信-JSAPI下单接口";

        try {
            System.out.println(label + " 请求参数:" +  JSON.toJSONString(request));

            // 调用下单方法，得到应答
            PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
            System.out.println(label + " 返回参数:" +  JSON.toJSONString(response));
        } catch (Exception e) { // 发送HTTP请求失败
            System.err.println(e);
        }
    }

    /**
     * 初始化商户配置
     * Created by kz on 2024/9/24 11:47.
     */
    private Config getWechatConfig(){
        //使用微信新的公钥模式（新入网的，强制使用这种模式）
        RSAPublicKeyConfig.Builder builder = new RSAPublicKeyConfig.Builder()
                .merchantId("")     //微信商户号
                .publicKeyId("")    //微信公钥ID
                .publicKeyFromPath("")  //微信公钥证书路径
                .merchantSerialNumber("")   //微信证书序列号
                .privateKeyFromPath("")     //微信私钥证书路径
                .apiV3Key("");          //微信API V3密钥
        return builder.build();
    }

    //微信小程序获取OpenID
    @Test
    public void wechatMiniGetOpenID(){
        String code = "微信小程序 wx.login 接口返回的code";

        String wechatMiniAppid = "微信小程序appId";
        String wechatMiniAppSecret = "微信小程序开发密钥";

        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code"
                , wechatMiniAppid , wechatMiniAppSecret , code);
        System.out.println("微信获取openId 请求地址:" + url);
        String httpReponse = HttpUtil.get(url);
        System.out.println("微信获取openId 返回参数:" + httpReponse);
    }
}