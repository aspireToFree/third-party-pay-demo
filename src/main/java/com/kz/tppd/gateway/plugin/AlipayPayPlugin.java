package com.kz.tppd.gateway.plugin;

import com.alibaba.fastjson.JSON;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.kz.tppd.common.enums.ChannelCodeEnum;
import com.kz.tppd.common.enums.ChannelStatusEnum;
import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.enums.PayMethodEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.utils.AlipayUtil;
import com.kz.tppd.trade.dto.request.*;
import com.kz.tppd.trade.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 支付宝直联 通道插件
 * @author kz
 */
@Slf4j
@Component
public class AlipayPayPlugin extends BaseChannelPayPlugin {

    /** 应用公网地址 */
    @Value("${server.publicAddress}")
    private String publicAddress;

    /** 支付宝接口请求地址 */
    @Value("${alipay.serverUrl}")
    private String serverUrl;

    /** 支付宝appId */
    @Value("${alipay.appId}")
    private String appId;

    /** 私钥 */
    @Value("${alipay.privateKey}")
    private String privateKey;

    /** 公钥 */
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Override
    public String getChannelCode() {
        return ChannelCodeEnum.ALIPAY.getCode();
    }

    /**
     * 统一下单
     * 支付宝生活号、支付宝小程序接口文档：https://opendocs.alipay.com/mini/6039ed0c_alipay.trade.create?scene=de4d6a1e0c6e423b9eefa7c3a6dcb7a5&pathHash=779dc517
     * APP支付接口文档：https://opendocs.alipay.com/open/cd12c885_alipay.trade.app.pay?pathHash=ab686e33&ref=api&scene=20
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz
     */
    @Override
    public UnifiedOrderResponseDTO unifiedOrder(UnifiedOrderRequestDTO requestDTO) {
        // 初始化SDK
        AlipayClient alipayClient = getAlipayClient(requestDTO);

        // 构造请求参数以调用接口
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        AlipayTradeCreateModel model = new AlipayTradeCreateModel();

        // 设置商户订单号
        model.setOutTradeNo(requestDTO.getOrderNo());

        //支付宝生活号、支付宝小程序
        if(PayMethodEnum.ALIPAY_PUBLIC.equals(requestDTO.getPayMethodEnum()) || PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
            // 设置产品码
            model.setProductCode("JSAPI_PAY");

            if(StringUtils.isNotBlank(requestDTO.getBuyerId())){
                model.setBuyerId(requestDTO.getBuyerId());
            } else if(StringUtils.isNotBlank(requestDTO.getOpenId())){
                model.setBuyerOpenId(requestDTO.getOpenId());
            }
        }
        //支付宝APP支付
        else if(PayMethodEnum.ALIPAY_APP.equals(requestDTO.getPayMethodEnum())){
            // 设置产品码
            model.setProductCode("QUICK_MSECURITY_PAY");
        } else {
            throw new BaseException(CommonErrorEnum.BUSINESS_ERROR.getCode() , "支付宝通道不支持的支付方式["+requestDTO.getPayMethodEnum()+"]");
        }

        // 设置订单总金额
        model.setTotalAmount(requestDTO.getPayAmount().toPlainString());
        // 设置订单标题
        model.setSubject(requestDTO.getTradeDesc());
        request.setBizModel(model);
        //后台回调地址
        request.setNotifyUrl(publicAddress + "/channel/alipayNotify/payNotify.html");

        String label = "支付宝-统一下单接口";

        AlipayTradeCreateResponse response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = alipayClient.execute(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            log.error(label + "返回异常" , e);
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , e.getMessage());
        }

        //调用失败
        if (!response.isSuccess()) {
            UnifiedOrderResponseDTO responseDTO = new UnifiedOrderResponseDTO();
            responseDTO.setChannelOrderNo(response.getTradeNo());
            setFailResponseDTO(responseDTO , response);
            return responseDTO;
        }

        UnifiedOrderResponseDTO responseDTO = new UnifiedOrderResponseDTO();
        //通道订单号
        responseDTO.setChannelOrderNo(response.getTradeNo());

        //支付宝生活号、支付宝小程序
        if(PayMethodEnum.ALIPAY_PUBLIC.equals(requestDTO.getPayMethodEnum()) || PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
            responseDTO.setAttach(response.getTradeNo());
        }
        //支付宝APP支付
        else if(PayMethodEnum.ALIPAY_APP.equals(requestDTO.getPayMethodEnum())){
            responseDTO.setAttach(response.getBody());
        }
        return responseDTO;
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
        // 初始化SDK
        AlipayClient alipayClient = getAlipayClient(requestDTO);

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单号
        model.setOutTradeNo(requestDTO.getOrderNo());
        model.setTradeNo(requestDTO.getChannelOrderNo());

        request.setBizModel(model);

        String label = "支付宝-支付订单查询接口";

        AlipayTradeQueryResponse response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = alipayClient.execute(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            //超时等其他异常，抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR, e);
        }

        if(!response.isSuccess()){
            //订单不存在，支付宝统一下单不用调用支付宝的接口下单，所以未支付就是订单不存在
            if(StringUtils.equals(response.getSubCode() , "ACQ.TRADE_NOT_EXIST")){
                return new PayOrderQueryResponseDTO(ChannelStatusEnum.NOTPAY);
            }
            //非业务异常，抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "支付宝通道支付订单查询异常["+response.getSubMsg()+"]");
        }

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        responseDTO.setChannelOrderNo(response.getTradeNo());

        //支付成功
        if("TRADE_SUCCESS".equals(response.getTradeStatus())){
        }
        //等待买家付款
        else if("WAIT_BUYER_PAY".equals(response.getTradeStatus())) {
            return new PayOrderQueryResponseDTO(ChannelStatusEnum.NOTPAY);
        } else if("TRADE_CLOSED".equals(response.getTradeStatus())) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(response.getTradeStatus());
            responseDTO.setErrorMessage("交易关闭");
        } else if("TRADE_FINISHED".equals(response.getTradeStatus())) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(response.getTradeStatus());
            responseDTO.setErrorMessage("交易结束，不可退款");
        } else {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "支付宝通道未知的交易状态["+response.getTradeStatus()+"]");
        }
        return responseDTO;
    }

    /**
     * 退款
     * 接口地址：https://opendocs.alipay.com/apis/api_1/alipay.trade.refund
     * **/
    @Override
    public RefundResponseDTO refund(RefundRequestDTO requestDTO) {
        // 初始化SDK
        AlipayClient alipayClient = getAlipayClient(requestDTO);

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        //原订单号
        model.setOutTradeNo(requestDTO.getOriginalOrderNo());
        model.setTradeNo(requestDTO.getOriginalChannelOrderNo());

        //退款请求单号
        model.setOutRequestNo(requestDTO.getOrderNo());

        //退款金额
        model.setRefundAmount(requestDTO.getRefundAmount().toPlainString());
        //退款原因
        model.setRefundReason(requestDTO.getRefundReason());

        request.setBizModel(model);
        //退款没得后台回调

        String label = "支付宝-退款接口";

        AlipayTradeRefundResponse response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = alipayClient.execute(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            log.error("支付宝退款异常" , e);
            //超时等异常，先当做退款请求成功，通过退款查询来确认最终结果，防止重复退款，造成资金损失
            return new RefundResponseDTO();
        }

        //调用失败
        if (!response.isSuccess()) {
            RefundResponseDTO responseDTO = new RefundResponseDTO();
            responseDTO.setChannelOrderNo(response.getTradeNo());
            setFailResponseDTO(responseDTO , response);
            return responseDTO;
        }

        //TODO 只是退款请求成功，真正的退款结果，需要另外查询
        RefundResponseDTO refundResponseDTO = new RefundResponseDTO(ChannelStatusEnum.REQUEST_SUCCESS);
        //通道订单号
        refundResponseDTO.setChannelOrderNo(response.getTradeNo());
        return refundResponseDTO;
    }

    /**
     * 退款查询
     * 接口地址：https://opendocs.alipay.com/mini/4a0fb7bf_alipay.trade.fastpay.refund.query?scene=common&pathHash=2e6cbb7c
     * **/
    @Override
    public RefundQueryResponseDTO refundQuery(RefundQueryRequestDTO requestDTO) {
        // 初始化SDK
        AlipayClient alipayClient = getAlipayClient(requestDTO);

        // 构造请求参数以调用接口
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();

        // 设置订单号
        model.setOutRequestNo(requestDTO.getOrderNo());

        //原支付订单号
        model.setOutTradeNo(requestDTO.getOriginalOrderNo());
        model.setTradeNo(requestDTO.getOriginalChannelOrderNo());

        request.setBizModel(model);

        String label = "支付宝-退款订单查询接口";

        AlipayTradeFastpayRefundQueryResponse response;
        try {
            log.info(label + " 请求参数:{}", JSON.toJSONString(request));

            response = alipayClient.execute(request);

            log.info(label + " 返回参数:{}", JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            //超时等其他异常，抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR, e);
        }

        if(!response.isSuccess()){
            //非业务异常，抛出异常，不修改自己系统的业务结果
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "支付宝通道退款查询异常["+response.getSubMsg()+"]");
        }

        //文档 refundStatus 枚举值只有 REFUND_SUCCESS，其他情况抛异常
        if(!StringUtils.equals(response.getRefundStatus() , "REFUND_SUCCESS")){
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "支付宝通道未知的退款状态["+response.getRefundStatus()+"]");
        }

        RefundQueryResponseDTO responseDTO = new RefundQueryResponseDTO();
        responseDTO.setChannelOrderNo(response.getTradeNo());
        return responseDTO;
    }

    /**
     * 获取 支付宝公共请求参数
     * @param basePayRequestDTO 基础请求参数DTO
     * @return 支付宝公共请求参数
     * Created by kz on 2024/9/24 11:47.
     */
    private AlipayClient getAlipayClient(BasePayRequestDTO basePayRequestDTO){
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(serverUrl);
        alipayConfig.setFormat(AlipayUtil.FORMAT);
        alipayConfig.setCharset(AlipayUtil.CHARSET);
        alipayConfig.setSignType(AlipayUtil.SIGN_TYPE);

        //appId、privateKey、alipayPublicKey要弄成动态的，也可以从 requestDTO中传过来动态获取
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);

        if(alipayConfig.getServerUrl().contains("openapi-sandbox.dl.alipaydev.com")){
            log.info("当前为支付宝沙箱环境");
        }

        log.info("serverUrl：{}，appId：{}" , alipayConfig.getServerUrl() , alipayConfig.getAppId());
        try {
            return new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            throw new BaseException("初始化支付宝参数异常" , e);
        }
    }

    /**
     * 设置错误返回参数DTO
     * @param response 支付宝返回参数
     * Created by kz on 2024/10/6 17:44.
     */
    private void setFailResponseDTO(BasePayResponseDTO responseDTO , AlipayResponse response){
        responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
        responseDTO.setErrorCode(StringUtils.isNotBlank(response.getSubCode())?response.getSubCode():response.getCode());
        responseDTO.setErrorMessage(StringUtils.isNotBlank(response.getSubMsg())?response.getSubMsg():response.getMsg());
    }
}