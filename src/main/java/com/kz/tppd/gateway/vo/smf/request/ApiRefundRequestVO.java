package com.kz.tppd.gateway.vo.smf.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * API - 退款请求参数VO
 * @author kz
 * @date 2022/8/11 11:18
 */
@Getter
@Setter
@ToString
public class ApiRefundRequestVO implements Serializable {

    private static final long serialVersionUID = 2155281398488694176L;

    /** 扫码富支付订单号 **/
    private String payOrderNo;

    /** 合作方支付订单号 **/
    private String payOutTradeNo;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 退款金额 **/
    private BigDecimal refundAmount;

    /** 后台回调地址 **/
    private String notifyUrl;

    /** 退款说明 **/
    private String refundRemark;
}