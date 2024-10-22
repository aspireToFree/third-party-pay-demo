package com.kz.tppd.gateway.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.kz.tppd.common.constants.BaseConstant;
import com.kz.tppd.common.enums.ChannelStatusEnum;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

/**
 * 微信通道插件回调 controller
 * @author kz
 * @date 2019/2/14 14:43.
 */
@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/channel/wechatNotify")
public class WechatPayPluginNotifyController {

    /** 微信商户号 */
    @Value("${wechat.merchantId}")
    private String merchantId;

    /** 微信私钥文件路径 */
    @Value("${wechat.privateKeyPath}")
    private String privateKeyPath;

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
    * @param request http request
    * Created by kz on 2019/2/14 14:50.
    */
    @RequestMapping(value = "payNotify.html")
    public ResponseEntity.BodyBuilder payNotify(HttpServletRequest request){
        //设置日志号
        MDC.put(BaseConstant.TRACE_LOG_ID, IDCreator.randomUUID());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info(headerName + ": " + headerValue);
        }

        String requestBody = getBodyString(request , "UTF-8");

        String label = "微信支付后台回调";

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader("Wechatpay-Serial"))
                .nonce(request.getHeader("Wechatpay-Nonce"))
                .signature(request.getHeader("Wechatpay-Signature"))
                .timestamp(request.getHeader("Wechatpay-Timestamp"))
                .body(requestBody)
                .build();

        log.info(label + " requestParam:{}" , JSON.toJSON(requestParam));

        // 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
        // 没有的话，则构造一个
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        }

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
            return ResponseEntity.status(HttpStatus.OK);
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
            return ResponseEntity.status(HttpStatus.OK);
        }

        try{
            orderResultProcess.payOrderResultProcess(responseDTO);
        } catch (Exception e){
            log.error(label + "异常" , e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY);
        }

        // 处理成功，返回 200 OK 状态码
        return ResponseEntity.status(HttpStatus.OK);
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