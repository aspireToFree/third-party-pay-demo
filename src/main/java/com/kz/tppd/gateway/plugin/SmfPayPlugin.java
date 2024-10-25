package com.kz.tppd.gateway.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kz.tppd.common.enums.*;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.dto.SmfKeyParamDTO;
import com.kz.tppd.gateway.utils.SmfUtil;
import com.kz.tppd.gateway.vo.smf.request.ApiCashierOrderRequestVO;
import com.kz.tppd.gateway.vo.smf.request.ApiQueryPayOrderRequestVO;
import com.kz.tppd.gateway.vo.smf.request.ApiQueryRefundOrderRequestVO;
import com.kz.tppd.gateway.vo.smf.request.ApiRefundRequestVO;
import com.kz.tppd.gateway.vo.smf.response.*;
import com.kz.tppd.trade.dto.request.*;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundResponseDTO;
import com.kz.tppd.trade.dto.response.UnifiedOrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 扫码富间联 通道插件
 * @author kz
 */
@Slf4j
@Component
public class SmfPayPlugin extends BaseChannelPayPlugin {

    /** 应用公网地址 */
    @Value("${server.publicAddress}")
    private String publicAddress;

    /** 接口请求地址 */
    @Value("${smf.serverUrl}")
    private String serverUrl;

    /** 平台商户号 */
    @Value("${smf.platformMercCode}")
    private String platformMercCode;

    /** 签名密钥 */
    @Value("${smf.signKey}")
    private String signKey;

    /** 报文密钥 */
    @Value("${smf.msgKey}")
    private String msgKey;

    /** 交易商户号 */
    @Value("${smf.tradeMercCode}")
    private String tradeMercCode;

    @Override
    public String getChannelCode() {
        return ChannelCodeEnum.SMF.getCode();
    }

    /**
     * 统一下单
     * 接口地址：https://www.yuque.com/lianhou/openapi/xwnlbb?singleDoc#kmJeX
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz
     */
    @Override
    public UnifiedOrderResponseDTO unifiedOrder(UnifiedOrderRequestDTO requestDTO) {
        SmfProductCapabilityEnum smfProductCapabilityEnum = requestDTO.getPayMethodEnum().getSmfProductCapabilityEnum();
        if(smfProductCapabilityEnum == null){
            throw new BaseException(CommonErrorEnum.BUSINESS_ERROR.getCode() , "扫码富通道不支持的支付方式["+requestDTO.getPayMethodEnum()+"]");
        }

        // 获取SMF密钥参数DTO
        SmfKeyParamDTO smfKeyParamDTO = getSmfKeyParamDTO(requestDTO);

        ApiCashierOrderRequestVO requestVO = new ApiCashierOrderRequestVO();
        requestVO.setMercCode(tradeMercCode);
        requestVO.setProductCapability(smfProductCapabilityEnum.getCode());
        requestVO.setOrderAmount(requestDTO.getPayAmount());
        requestVO.setClientType(requestDTO.getPayMethodEnum().getClientTypeEnum().getCode());

        //TODO 扫码富暂不支持支付宝openId，只支持支付宝buyerId
        if(PayMethodEnum.ALIPAY_PUBLIC.equals(requestDTO.getPayMethodEnum()) || PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
            requestVO.setOpenId(requestDTO.getBuyerId());
        } else {
            requestVO.setOpenId(requestDTO.getOpenId());
        }

        requestVO.setAppId(requestDTO.getAppId());
        requestVO.setTradeDesc(requestDTO.getTradeDesc());
        requestVO.setTermIp(requestDTO.getClientIp());
        requestVO.setNotifyUrl(publicAddress + "/channel/smfNotify/payNotify");
        requestVO.setOutTradeNo(System.currentTimeMillis()+"");

        String reqBody = JSON.toJSONString(requestVO);

        ApiMainResponseVO apiMainResponseVO = SmfUtil.sendChannel("统一下单" , serverUrl + "/api/trade/cashierOrder.html" , reqBody , smfKeyParamDTO);

        //调用失败
        if (!apiMainResponseVO.isSuccess()) {
            return new UnifiedOrderResponseDTO(ChannelStatusEnum.FAIL , apiMainResponseVO.getRespCode() , apiMainResponseVO.getRespMsg());
        }

        if(StringUtils.equals(smfKeyParamDTO.getPlatformMercCode() , "MER0000000004601")){
            if(PayMethodEnum.YSF_PUBLIC.equals(requestDTO.getPayMethodEnum())){
                log.info("当前为扫码富测试环境的MOCK参数，会自动模拟成功，不会真实扣费；返回的payUrl参数也不能调起云闪付的支付。");
            } else {
                log.info("当前为扫码富测试环境的MOCK参数，会自动模拟成功，不会真实扣费；返回的attach参数也不能调起微信、支付宝的支付。");
            }
        }

        ApiCashierOrderResponseVO responseVO = JSONObject.parseObject(apiMainResponseVO.getRespData() , ApiCashierOrderResponseVO.class);

        UnifiedOrderResponseDTO responseDTO = new UnifiedOrderResponseDTO();
        //通道订单号
        responseDTO.setChannelOrderNo(responseVO.getOrderNo());
        responseDTO.setAttach(responseVO.getAttach());
        responseDTO.setPayUrl(responseVO.getPayUrl());
        return responseDTO;
    }

    /**
     * 支付订单查询
     * 接口地址：https://www.yuque.com/lianhou/openapi/xwnlbb?singleDoc#DuhIV
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz on
     */
    @Override
    public PayOrderQueryResponseDTO orderQuery(PayOrderQueryRequestDTO requestDTO) {
        // 获取SMF密钥参数DTO
        SmfKeyParamDTO smfKeyParamDTO = getSmfKeyParamDTO(requestDTO);

        ApiQueryPayOrderRequestVO requestVO = new ApiQueryPayOrderRequestVO();
        requestVO.setOrderNo(requestDTO.getChannelOrderNo());
        requestVO.setOutTradeNo(requestDTO.getOrderNo());

        String reqBody = JSON.toJSONString(requestVO);

        ApiMainResponseVO apiMainResponseVO = SmfUtil.sendChannel("支付订单查询" , serverUrl + "/api/trade/queryPayOrder.html" , reqBody , smfKeyParamDTO);
        //非业务异常，只抛出异常，不修改订单结果
        if (!apiMainResponseVO.isSuccess()) {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode() , apiMainResponseVO.getRespMsg());
        }

        ApiQueryPayOrderResponseVO responseVO = JSONObject.parseObject(apiMainResponseVO.getRespData() , ApiQueryPayOrderResponseVO.class);

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        responseDTO.setChannelOrderNo(responseVO.getOrderNo());

        //支付成功
        if("SUCCESS".equals(responseVO.getPayStatus())){
        }
        //等待买家付款
        else if("PROCESSING".equals(responseVO.getPayStatus())) {
            return new PayOrderQueryResponseDTO(ChannelStatusEnum.NOTPAY);
        } else if("CLOSED".equals(responseVO.getPayStatus())) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(responseVO.getPayStatus());
            responseDTO.setErrorMessage("交易关闭");
        } else if("FAIL".equals(responseVO.getPayStatus())) {
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(responseVO.getPayStatus());
            responseDTO.setErrorMessage(responseVO.getFailMessage());
        } else {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "扫码富通道未知的支付状态["+responseVO.getPayStatus()+"]");
        }
        return responseDTO;
    }

    /**
     * 退款
     * 接口地址：https://www.yuque.com/lianhou/openapi/xwnlbb?singleDoc#IyruX
     * **/
    @Override
    public RefundResponseDTO refund(RefundRequestDTO requestDTO) {
        // 获取SMF密钥参数DTO
        SmfKeyParamDTO smfKeyParamDTO = getSmfKeyParamDTO(requestDTO);

        ApiRefundRequestVO requestVO = new ApiRefundRequestVO();
        requestVO.setPayOrderNo(requestDTO.getOriginalChannelOrderNo());
        requestVO.setPayOutTradeNo(requestDTO.getOriginalOrderNo());
        requestVO.setOutTradeNo(requestDTO.getOrderNo());
        requestVO.setRefundAmount(requestDTO.getRefundAmount());
        requestVO.setRefundRemark(requestDTO.getRefundReason());

        String reqBody = JSON.toJSONString(requestVO);

        ApiMainResponseVO apiMainResponseVO;
        try {
            apiMainResponseVO = SmfUtil.sendChannel("退款" , serverUrl + "/api/trade/refund.html" , reqBody , smfKeyParamDTO);
        } catch (Exception e) {
            log.error("扫码富退款异常" , e);
            //返回异常，先当做退款请求成功，通过退款查询来确认最终结果，防止重复退款，造成资金损失
            return new RefundResponseDTO();
        }

        if (!apiMainResponseVO.isSuccess()) {
            return new RefundResponseDTO(ChannelStatusEnum.FAIL , apiMainResponseVO.getRespCode() , apiMainResponseVO.getRespMsg());
        }

        if(StringUtils.equals(smfKeyParamDTO.getPlatformMercCode() , "MER0000000004601")){
            log.info("当前为扫码富测试环境的MOCK参数，会自动模拟退款成功，不会真实扣费。");
        }

        ApiRefundResponseVO responseVO = JSONObject.parseObject(apiMainResponseVO.getRespData() , ApiRefundResponseVO.class);

        RefundResponseDTO refundResponseDTO = new RefundResponseDTO();
        //通道订单号
        refundResponseDTO.setChannelOrderNo(responseVO.getOrderNo());

        //TODO 只是退款请求成功，真正的退款结果，需要另外查询
        if(StringUtils.equals(responseVO.getRefundStatus() , "REQUEST_SUCCESS")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        }
        else if(StringUtils.equals(responseVO.getRefundStatus() , "FAIL")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(responseVO.getRefundStatus());
            refundResponseDTO.setErrorMessage(responseVO.getFailMessage());
        } else {
            //其他未知状态，通过退款查询接口，查询结果
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        }
        return refundResponseDTO;
    }

    /**
     * 退款查询
     * 接口地址：https://www.yuque.com/lianhou/openapi/xwnlbb?singleDoc#c13fO
     * **/
    @Override
    public RefundQueryResponseDTO refundQuery(RefundQueryRequestDTO requestDTO) {
        // 获取SMF密钥参数DTO
        SmfKeyParamDTO smfKeyParamDTO = getSmfKeyParamDTO(requestDTO);

        ApiQueryRefundOrderRequestVO requestVO = new ApiQueryRefundOrderRequestVO();
        requestVO.setOrderNo(requestDTO.getChannelOrderNo());
        requestVO.setOutTradeNo(requestDTO.getOrderNo());

        String reqBody = JSON.toJSONString(requestVO);

        ApiMainResponseVO apiMainResponseVO = SmfUtil.sendChannel("退款查询" , serverUrl + "/api/trade/queryRefundOrder.html" , reqBody , smfKeyParamDTO);
        //非业务异常，只抛出异常，不修改订单结果
        if (!apiMainResponseVO.isSuccess()) {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode() , apiMainResponseVO.getRespMsg());
        }

        ApiQueryRefundOrderResponseVO responseVO = JSONObject.parseObject(apiMainResponseVO.getRespData() , ApiQueryRefundOrderResponseVO.class);

        RefundQueryResponseDTO refundResponseDTO = new RefundQueryResponseDTO();
        //通道订单号
        refundResponseDTO.setChannelOrderNo(responseVO.getOrderNo());

        String status = responseVO.getRefundStatus();
        //退款成功
        if(StringUtils.equals(status , "SUCCESS")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        }
        //处理中
        else if(StringUtils.equals(status , "PROCESSING")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.PROCESSING);
        } else if(StringUtils.equals(status , "FAIL")){
            refundResponseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            refundResponseDTO.setErrorCode(status);
            refundResponseDTO.setErrorMessage(responseVO.getFailMessage());
        } else {
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode(), "扫码富通道未知的退款状态["+status+"]");
        }
        return refundResponseDTO;
    }

    /**
     * 获取SMF密钥参数DTO
     * @param basePayRequestDTO 基础请求参数DTO
     * @return SMF密钥参数DTO
     * Created by kz on 2024/9/24 11:47.
     */
    private SmfKeyParamDTO getSmfKeyParamDTO(BasePayRequestDTO basePayRequestDTO){
        //platformMercCode、signKey、msgKey 要弄成动态的，也可以从 requestDTO中传过来动态获取

        SmfKeyParamDTO smfKeyParamDTO = new SmfKeyParamDTO();
        smfKeyParamDTO.setPlatformMercCode(platformMercCode);
        smfKeyParamDTO.setSignKey(signKey);
        smfKeyParamDTO.setMsgKey(msgKey);
        return smfKeyParamDTO;
    }
}