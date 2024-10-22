package com.kz.tppd.gateway.plugin;

import com.alibaba.fastjson.JSON;
import com.kz.tppd.common.enums.ChannelCodeEnum;
import com.kz.tppd.common.enums.ChannelStatusEnum;
import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.enums.PayMethodEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.trade.dto.request.*;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundResponseDTO;
import com.kz.tppd.trade.dto.response.UnifiedOrderResponseDTO;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.app.AppServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 微信直联 通道插件
 * @author kz
 */
@Slf4j
@Component
public class WechatPayPlugin extends BaseChannelPayPlugin {

    /** 应用公网地址 */
    @Value("${server.publicAddress}")
    private String publicAddress;

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

    @Override
    public String getChannelCode() {
        return ChannelCodeEnum.WECHAT.getCode();
    }

    /**
     * 统一下单
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz
     */
    @Override
    public UnifiedOrderResponseDTO unifiedOrder(UnifiedOrderRequestDTO requestDTO) {
        // 初始化商户配置
        Config config = getWechatConfig(requestDTO);

        //微信公众号、微信小程序
        if(PayMethodEnum.WECHAT_PUBLIC.equals(requestDTO.getPayMethodEnum()) || PayMethodEnum.WECHAT_MINI.equals(requestDTO.getPayMethodEnum())){
            return jsapi(requestDTO , config);
        }
        //微信APP支付
        else if(PayMethodEnum.WECHAT_APP.equals(requestDTO.getPayMethodEnum())){
            return appOrder(requestDTO , config);
        } else {
            throw new BaseException(CommonErrorEnum.BUSINESS_ERROR.getCode() , "支付宝通道不支持的支付方式["+requestDTO.getPayMethodEnum()+"]");
        }
    }

    /**
     * 微信JS API支付
     * 微信公众号、微信小程序接口文档：https://pay.weixin.qq.com/doc/v3/merchant/4012525057
     * @param requestDTO 请求参数DTO
     * @param config 公共配置
     * @return 返回结果
     * Created by kz on 2024/10/17 14:21.
     */
    private UnifiedOrderResponseDTO jsapi(UnifiedOrderRequestDTO requestDTO , Config config){
        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        //金额转分
        amount.setTotal(formatAmount(requestDTO.getPayAmount()));
        request.setAmount(amount);
        request.setAppid(requestDTO.getAppId());

        Payer payer = new Payer();
        payer.setOpenid(requestDTO.getOpenId());
        request.setPayer(payer);

        //如果是动态配置，从requestDTO中拿
        request.setMchid(merchantId);

        // 设置订单标题
        request.setDescription(requestDTO.getTradeDesc());
        //后台回调地址
        request.setNotifyUrl(publicAddress + "/channel/wechatNotify/payNotify.html");
        // 设置商户订单号
        request.setOutTradeNo(requestDTO.getOrderNo());

        String label = "微信-JSAPI下单接口";

        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            // 调用下单方法，得到应答
            PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
            log.info(label + " 返回参数:{}", JSON.toJSONString(response));

            UnifiedOrderResponseDTO responseDTO = new UnifiedOrderResponseDTO();
            responseDTO.setAttach(JSON.toJSONString(response));
            return responseDTO;
        } catch (HttpException e) { // 发送HTTP请求失败
            // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , e.getErrorCode() , e.getErrorMessage());
        } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
            // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (Exception e) {
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        }
    }

    /**
     * 微信APP下单
     * APP支付接口文档：https://pay.weixin.qq.com/doc/v3/merchant/4012525068
     * @param requestDTO 请求参数DTO
     * @param config 公共配置
     * @return 返回结果
     * Created by kz on 2024/10/17 14:21.
     */
    private UnifiedOrderResponseDTO appOrder(UnifiedOrderRequestDTO requestDTO , Config config){
        // 构建service
        AppServiceExtension service = new AppServiceExtension.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        com.wechat.pay.java.service.payments.app.model.PrepayRequest request = new com.wechat.pay.java.service.payments.app.model.PrepayRequest();
        com.wechat.pay.java.service.payments.app.model.Amount amount = new com.wechat.pay.java.service.payments.app.model.Amount();
        //金额转分
        amount.setTotal(formatAmount(requestDTO.getPayAmount()));
        request.setAmount(amount);
        request.setAppid(requestDTO.getAppId());

        //如果是动态配置，从requestDTO中拿
        request.setMchid(merchantId);

        // 设置订单标题
        request.setDescription(requestDTO.getTradeDesc());
        //后台回调地址
        request.setNotifyUrl(publicAddress + "/channel/wechatNotify/payNotify.html");
        // 设置商户订单号
        request.setOutTradeNo(requestDTO.getOrderNo());

        String label = "微信-APP下单接口";

        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            // 调用下单方法，得到应答
            com.wechat.pay.java.service.payments.app.model.PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
            log.info(label + " 返回参数:{}", JSON.toJSONString(response));

            UnifiedOrderResponseDTO responseDTO = new UnifiedOrderResponseDTO();
            responseDTO.setAttach(JSON.toJSONString(response));
            return responseDTO;
        } catch (HttpException e) { // 发送HTTP请求失败
            // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , e.getErrorCode() , e.getErrorMessage());
        } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
            // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义

            log.error(label + "异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (Exception e) {
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        }
    }

    /**
     * 支付订单查询
     * 接口地址：https://opendocs.alipay.com/mini/824da765_alipay.trade.refund?scene=common&pathHash=b18b975d
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz on
     */
    @Override
    public PayOrderQueryResponseDTO orderQuery(PayOrderQueryRequestDTO requestDTO) {
        // 初始化商户配置
        Config config = getWechatConfig(requestDTO);

        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(config).build();

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        //如果是动态配置，从requestDTO中拿
        request.setMchid(merchantId);
        request.setOutTradeNo(requestDTO.getOrderNo());

        String label = "微信-支付订单查询接口";

        Transaction response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = service.queryOrderByOutTradeNo(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (Exception e) {
            //抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR, e);
        }

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        responseDTO.setChannelOrderNo(response.getTransactionId());

        String status = response.getTradeState().name();
        //支付成功
        if("SUCCESS".equals(status)){
        }
        //等待买家付款
        else if("NOTPAY".equals(status) || "USERPAYING".equals(status)) {
            return new PayOrderQueryResponseDTO(ChannelStatusEnum.NOTPAY);
        } else if("CLOSED".equals(status)) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(status);
            responseDTO.setErrorMessage("交易关闭");
        }
        //支付失败
        else if("PAYERROR".equals(status)) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(status);
            responseDTO.setErrorMessage(response.getTradeStateDesc());
        } else {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "微信通道未知的交易状态["+status+"]");
        }
        return responseDTO;
    }

    /**
     * 退款
     * 接口地址：https://pay.weixin.qq.com/doc/v3/merchant/4012556445
     * **/
    @Override
    public RefundResponseDTO refund(RefundRequestDTO requestDTO) {
        // 初始化商户配置
        Config config = getWechatConfig(requestDTO);

        // 构建service
        RefundService service = new RefundService.Builder().config(config).build();

        CreateRequest request = new CreateRequest();

        //原订单号
        request.setOutTradeNo(requestDTO.getOriginalOrderNo());
        request.setTransactionId(requestDTO.getOriginalChannelOrderNo());

        //退款请求单号
        request.setOutRefundNo(requestDTO.getOrderNo());

        //退款金额
        AmountReq amount = new AmountReq();
        amount.setRefund(formatAmount(requestDTO.getRefundAmount()).longValue());
        request.setAmount(amount);
        //退款原因
        request.setReason(requestDTO.getRefundReason());

        String label = "微信-退款接口";

        Refund response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = service.create(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (HttpException e) { // 发送HTTP请求失败
            // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义

            log.error(label + "异常" , e);
            //Http请求异常，返回失败
            return new RefundResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义

            log.error(label + "异常" , e);
            //Http返回异常，先当做退款请求成功，通过退款查询来确认最终结果，防止重复退款，造成资金损失
            return new RefundResponseDTO();
        } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
            // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义

            log.error(label + "异常" , e);
            //通道解析异常，返回失败
            return new RefundResponseDTO(ChannelStatusEnum.FAIL , CommonErrorEnum.CHANNEL_ERROR.getCode() , e.getMessage());
        } catch (Exception e) {
            log.error(label + "异常" , e);
            //返回异常，先当做退款请求成功，通过退款查询来确认最终结果，防止重复退款，造成资金损失
            return new RefundResponseDTO();
        }

        RefundResponseDTO refundResponseDTO = new RefundResponseDTO();
        //通道订单号
        refundResponseDTO.setChannelOrderNo(response.getRefundId());

        String status = response.getStatus().name();
        //退款成功
        if(StringUtils.equals(status , "SUCCESS")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款异常");
        }
        //TODO 只是退款请求成功，真正的退款结果，需要另外查询
        else if(StringUtils.equals(status , "PROCESSING")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.REQUEST_SUCCESS);
        } else if(StringUtils.equals(status , "ABNORMAL")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款异常");
        } else if(StringUtils.equals(status , "CLOSED")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款关闭");
        } else {
            //其他未知状态，通过退款查询接口，查询结果
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.REQUEST_SUCCESS);
        }
        return refundResponseDTO;
    }

    /**
     * 退款查询
     * 接口地址：https://pay.weixin.qq.com/doc/v3/merchant/4012556554
     * **/
    @Override
    public RefundQueryResponseDTO refundQuery(RefundQueryRequestDTO requestDTO) {
        // 初始化商户配置
        Config config = getWechatConfig(requestDTO);

        // 构建service
        RefundService service = new RefundService.Builder().config(config).build();

        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();

        //退款请求单号
        request.setOutRefundNo(requestDTO.getOrderNo());


        String label = "微信-退款订单查询接口";

        Refund response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = service.queryByOutRefundNo(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (Exception e) {
            //抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR, e);
        }

        RefundQueryResponseDTO refundResponseDTO = new RefundQueryResponseDTO();
        //通道订单号
        refundResponseDTO.setChannelOrderNo(response.getRefundId());

        String status = response.getStatus().name();
        //退款成功
        if(StringUtils.equals(status , "SUCCESS")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款异常");
        }
        //处理中
        else if(StringUtils.equals(status , "PROCESSING")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.PROCESSING);
        } else if(StringUtils.equals(status , "ABNORMAL")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款异常");
        } else if(StringUtils.equals(status , "CLOSED")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage("退款关闭");
        } else {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "微信通道未知的退款状态["+status+"]");
        }
        return refundResponseDTO;
    }

    /**
     * 初始化商户配置
     * @param basePayRequestDTO 基础请求参数DTO
     * @return 微信公共请求参数
     * Created by kz on 2024/9/24 11:47.
     */
    private Config getWechatConfig(BasePayRequestDTO basePayRequestDTO){
        //merchantId、privateKeyPath、merchantSerialNumber、apiV3Key 要弄成动态的，也可以从 requestDTO中传过来动态获取

        return new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                //.privateKey(privateKey)   //私钥字符串
                .privateKeyFromPath(privateKeyPath) //私钥绝对路径
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();
    }

    /**
     * 金额转给分为单位
     * @param amount 金额
     * @return 分为单位的金额
     * Created by kz on 2024/10/17 13:54.
     */
    private Integer formatAmount(BigDecimal amount){
        return amount.multiply(new BigDecimal("100")).stripTrailingZeros().intValue();
    }
}