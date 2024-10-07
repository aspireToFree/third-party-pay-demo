package com.kz.tppd.gateway.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.kz.tppd.common.constants.BaseConstant;
import com.kz.tppd.common.enums.ChannelStatusEnum;
import com.kz.tppd.gateway.utils.AlipayUtil;
import com.kz.tppd.gateway.utils.NotifyUtil;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.service.OrderResultService;
import com.kz.tppd.utils.IDCreator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝通道插件回调 controller
 * @author kz
 * @date 2019/2/14 14:43.
 */
@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/channel/alipayNotify")
public class AlipayPayPluginNotifyController {

    /** 支付宝公钥证书路径 */
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Resource
    private OrderResultService orderResultProcess;

    /**
    * 支付后台回调
    * 接口地址：https://opendocs.alipay.com/mini/080p65?pathHash=216e5c27
    * @param request http request
    * @param response http response
    * Created by kz on 2019/2/14 14:50.
    */
    @RequestMapping(value = "payNotify.html")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //设置日志号
        MDC.put(BaseConstant.TRACE_LOG_ID, IDCreator.randomUUID());

        Map<String, String> param = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            param.put(name, valueStr);
        }

        String label = "支付宝支付后台回调";

        log.info(label + " param:{}" , param);

        if(!AlipaySignature.rsaCertCheckV1(param, alipayPublicKey, AlipayUtil.CHARSET , AlipayUtil.SIGN_TYPE)){
            log.error("签名错误");
            NotifyUtil.returnPrintWriter(response ,"fail");
            return;
        }

        //交易状态
        String tradeStatus = param.get("trade_status");

        PayOrderQueryResponseDTO responseDTO = new PayOrderQueryResponseDTO();
        //支付宝交易号
        responseDTO.setChannelOrderNo(param.get("trade_no"));
        //平台订单号
        responseDTO.setOrderNo(param.get("out_trade_no"));

        //支付成功
        if("TRADE_SUCCESS".equals(tradeStatus)){
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.SUCCESS);
        } else if("TRADE_FINISHED".equals(tradeStatus)){
            //交易结束，不可退款（不做业务逻辑处理）
            NotifyUtil.returnPrintWriter(response ,"success");
            return;
        } else if("TRADE_CLOSED".equals(tradeStatus)){
            //超时未付款或全额退款（全额退款的情况：订单会先通知支付成功，这里会先变更订单状态为成功，后续全额退款再通知交易关闭，判断订单是否已经支付，如果已支付，就直接跳过全额退款交易关闭的这种情况）
            responseDTO.setChannelStatusEnum(ChannelStatusEnum.FAIL);
            responseDTO.setErrorCode(tradeStatus);
            responseDTO.setErrorMessage("交易超时关闭");
        } else if("WAIT_BUYER_PAY".equals(tradeStatus)){
            //未支付（不做业务逻辑处理）
            NotifyUtil.returnPrintWriter(response ,"success");
            return;
        } else {
            log.error("未知状态[{}]" , tradeStatus);
            NotifyUtil.returnPrintWriter(response ,"fail");
            return;
        }

        try{
            orderResultProcess.payOrderResultProcess(responseDTO);
        } catch (Exception e){
            log.error("支付宝支付后台回调异常" , e);
            NotifyUtil.returnPrintWriter(response ,"fail");
            return;
        }

        NotifyUtil.returnPrintWriter(response ,"success");
    }
}