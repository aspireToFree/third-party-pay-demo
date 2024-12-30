package com.kz.tppd.test;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import org.junit.Test;

/**
 * 支付宝直联测试
 * @date 2024/12/19 10:34
 */
public class AlipayDirectTest {

    //支付宝JSAPI测试
    @Test
    public void jsapi(){
        AlipayClient alipayClient = getAlipayClient();

        // 构造请求参数以调用接口
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        AlipayTradeCreateModel model = new AlipayTradeCreateModel();

        // 设置商户订单号
        model.setOutTradeNo(System.currentTimeMillis() + "");

        // 设置产品码
        model.setProductCode("JSAPI_PAY");

        //user_id、open_id传一个就可以了
        model.setBuyerId("用户user_id");
        model.setBuyerOpenId("用户open_id");

        // 设置订单总金额
        model.setTotalAmount("1");
        // 设置订单标题
        model.setSubject("测试");
        request.setBizModel(model);
        //后台回调地址
        request.setNotifyUrl("后台回调地址");

        String label = "支付宝-统一下单接口";

        AlipayTradeCreateResponse response;
        try {
            System.out.println(label + " 请求参数:" + JSON.toJSONString(request));

            response = alipayClient.execute(request);

            System.out.println(label + " 返回参数:" + JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            System.err.println(e);
            return;
        }

        if (response.isSuccess()) {
            //...下单成功，业务逻辑处理
        } else {
            //...下单失败，业务逻辑处理
        }
    }

    /**
     * 获取 支付宝公共请求参数
     * @return 支付宝公共请求参数
     */
    private AlipayClient getAlipayClient(){
        String privateKey  = "<-- 请填写您的应用私钥，例如：MIIEvQIBADANB ... ... -->";
        String alipayPublicKey = "<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        alipayConfig.setAppId("<-- 请填写您的AppId，例如：2019091767145019 -->");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);

        System.out.println("serverUrl："+alipayConfig.getServerUrl()+"，appId：" + alipayConfig.getAppId());
        try {
            return new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            throw new RuntimeException("初始化支付宝参数异常" , e);
        }
    }
}