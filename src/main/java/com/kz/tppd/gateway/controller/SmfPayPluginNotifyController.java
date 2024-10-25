package com.kz.tppd.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.kz.tppd.common.constants.BaseConstant;
import com.kz.tppd.common.enums.ChannelStatusEnum;
import com.kz.tppd.common.utils.AlgorithmUtils;
import com.kz.tppd.gateway.utils.NotifyUtil;
import com.kz.tppd.gateway.vo.smf.request.ApiMainRequestVO;
import com.kz.tppd.gateway.vo.smf.request.ApiPayNotifyVO;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.service.OrderResultService;
import com.kz.tppd.utils.IDCreator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 扫码富通道插件回调 controller
 * @author kz
 * @date 2019/2/14 14:43.
 */
@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/channel/smfNotify/")
public class SmfPayPluginNotifyController {

    /** 签名密钥 */
    @Value("${smf.signKey}")
    private String signKey;

    /** 报文密钥 */
    @Value("${smf.msgKey}")
    private String msgKey;

    @Resource
    private OrderResultService orderResultProcess;

    /**
    * 支付后台回调
    * 接口地址：https://www.yuque.com/lianhou/openapi/xwnlbb?singleDoc#JmcR6
    * @param request http request
    * @param response http response
    * Created by kz on 2019/2/14 14:50.
    */
    @RequestMapping(value = "payNotify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //设置日志号
        MDC.put(BaseConstant.TRACE_LOG_ID, IDCreator.randomUUID());

        String requestBody = NotifyUtil.getBodyString(request , "UTF-8");

        String label = "扫码富支付后台回调";

        log.info(label + " requestBody:{}" , requestBody);

        ApiMainRequestVO apiMainRequestVO = JSONObject.parseObject(requestBody , ApiMainRequestVO.class);
        String reqData = AlgorithmUtils.desDecryptFromBase64(apiMainRequestVO.getReqData() , msgKey);
        log.info("回调参数明文:{}" , reqData);

        apiMainRequestVO.setReqData(reqData);

        if(StringUtils.isBlank(apiMainRequestVO.getSign())){
            log.error(label + "签名为空");
            NotifyUtil.returnPrintWriter(response ,"{\"code\":\"fail\",\"message\":\"签名为空\"}");
            return;
        }

        String sign = AlgorithmUtils.getSHA256Hex(apiMainRequestVO.getSignData(signKey)).toUpperCase();
        if(!StringUtils.equals(apiMainRequestVO.getSign() , sign)){
            log.error(label + "验签失败");
            NotifyUtil.returnPrintWriter(response ,"{\"code\":\"fail\",\"message\":\"验签失败\"}");
            return;
        }

        ApiPayNotifyVO notifyVO = JSONObject.parseObject(apiMainRequestVO.getReqData() , ApiPayNotifyVO.class);

        String tradeStatus = notifyVO.getPayStatus();

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        responseDTO.setChannelOrderNo(notifyVO.getOrderNo());
        responseDTO.setOrderNo(notifyVO.getOutTradeNo());
        //支付成功
        if("SUCCESS".equals(tradeStatus)){
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        } else if("CLOSED".equals(tradeStatus)){
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(tradeStatus);
            responseDTO.setErrorMessage("交易超时关闭");
        } else if("FAIL".equals(tradeStatus)){
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(tradeStatus);
            responseDTO.setErrorMessage(notifyVO.getFailMessage());
        } else {
            log.error("未知状态[{}]" , tradeStatus);
            NotifyUtil.returnPrintWriter(response ,"fail");
            return;
        }

        try{
            orderResultProcess.payOrderResultProcess(responseDTO);
        } catch (Exception e){
            log.error(label + "异常" , e);
            NotifyUtil.returnPrintWriter(response ,"{\"code\":\"fail\",\"message\":\""+e.getMessage()+"\"}");
            return;
        }

        NotifyUtil.returnPrintWriter(response ,"{\"code\":\"0000\",\"message\":\"success\"}");
    }
}