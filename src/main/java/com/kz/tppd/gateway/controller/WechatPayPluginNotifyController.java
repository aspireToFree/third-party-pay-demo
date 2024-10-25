package com.kz.tppd.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.kz.tppd.common.constants.BaseConstant;
import com.kz.tppd.common.enums.ChannelStatusEnum;
import com.kz.tppd.gateway.utils.NotifyUtil;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.service.OrderResultService;
import com.kz.tppd.utils.IDCreator;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 微信通道插件回调 controller
 * @author kz
 * @date 2019/2/14 14:43.
 */
@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/channel/wechatNotify/")
public class WechatPayPluginNotifyController {

    /** 微信商户号 */
    @Value("${wechat.merchantId}")
    private String merchantId;

    /** 微信私钥文件路径 */
    @Value("${wechat.privateKeyPath}")
    private String privateKeyPath;

    /** 微信私钥证书字符串 */
    @Value("${wechat.privateKeyString}")
    private String privateKeyString;

    /** 商户证书序列号 */
    @Value("${wechat.merchantSerialNumber}")
    private String merchantSerialNumber;

    /** 商户APIV3密钥 */
    @Value("${wechat.apiV3Key}")
    private String apiV3Key;

    @Resource
    private OrderResultService orderResultProcess;

    /**
    * 支付后台回调
    * 接口地址：https://pay.weixin.qq.com/doc/v3/merchant/4012247562
    * @param wechatMercCode 微信商户号
    * @param request http request
    * Created by kz on 2019/2/14 14:50.
    */
    @RequestMapping(value = "payNotify/{wechatMercCode}")
    public ResponseEntity<String> payNotify(HttpServletRequest request , @PathVariable String wechatMercCode){
        //设置日志号
        MDC.put(BaseConstant.TRACE_LOG_ID, IDCreator.randomUUID());

        String requestBody = NotifyUtil.getBodyString(request , "UTF-8");

        String label = "微信支付后台回调";

        log.info(label);

        //要打印的请求头
        List<String> requestHeadList = Arrays.asList("wechatpay-serial" , "wechatpay-nonce" , "wechatpay-signature" , "wechatpay-timestamp" , "wechatpay-signature-type");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            if(requestHeadList.contains(headerName)){
                log.info("请求头 {}:{}" , headerName , headerValue);
            }
        }

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader("wechatpay-serial"))
                .nonce(request.getHeader("wechatpay-nonce"))
                .signature(request.getHeader("wechatpay-signature"))
                .timestamp(request.getHeader("wechatpay-timestamp"))
                .signType(request.getHeader("wechatpay-signature-type"))
                .body(requestBody)
                .build();

        log.info("请求body:{}" , requestBody);

        //TODO 需要动态获取的话，可以通过 wechatMercCode查询对应的微信商户密钥证书
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                //.privateKey(privateKeyString)   //私钥字符串
                .privateKeyFromPath(privateKeyPath)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();

        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);

        Transaction response;
        try {
            response = parser.parse(requestParam, Transaction.class);
        } catch (ValidationException e) {
            log.error("签名验证失败", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("签名验证失败");
        }
        log.info("明文：{}" , JSON.toJSONString(response));

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        //支付宝交易号
        responseDTO.setChannelOrderNo(response.getTransactionId());
        //平台订单号
        responseDTO.setOrderNo(response.getOutTradeNo());

        String status = response.getTradeState().name();
        //支付成功
        if("SUCCESS".equals(status)){
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        }
        //等待买家付款
        else if("NOTPAY".equals(status) || "USERPAYING".equals(status)) {
            //直接返回
            return ResponseEntity.status(HttpStatus.OK).body("成功");
        } else if("CLOSED".equals(status)) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(status);
            responseDTO.setErrorMessage("交易超时关闭");
        }
        //支付失败
        else if("PAYERROR".equals(status)) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(status);
            responseDTO.setErrorMessage(response.getTradeStateDesc());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("成功");
        }

        try{
            orderResultProcess.payOrderResultProcess(responseDTO);
        } catch (Exception e){
            log.error(label + "异常" , e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }

        // 处理成功，返回 200 OK 状态码
        return ResponseEntity.status(HttpStatus.OK).body("成功");
    }
}