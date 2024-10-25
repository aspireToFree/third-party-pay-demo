package com.kz.tppd.gateway.vo.smf.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API模块 支付回调
 * @author kz
 * @date 2022/8/12 17:27
 */
@Getter
@Setter
@ToString
public class ApiPayNotifyVO implements Serializable {

    private static final long serialVersionUID = 1449175777619525946L;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 平台订单号 **/
    private String orderNo;

    /** 支付状态 **/
    private String payStatus;

    /** 失败说明 **/
    private String failMessage;

    /** 订单金额 **/
    private String orderAmount;

    /** 商户手续费 **/
    private String fee;

    /** 商户号 **/
    private String mercCode;

    /** 支付时间 **/
    private String payTime;

    /** 卡类型 **/
    private String cardType;

    /** 三方订单号 **/
    private String thirdMercOrderNo;

    /** 三方商户单号 **/
    private String thirdOrderNo;

    /** 交易类型 **/
    private String tradeType;

    /** 支付方式 **/
    private String payMethod;

    /** 终端号 **/
    private String termNo;

    /** 终端序列号 **/
    private String termSn;

    /** 门店编号 **/
    private String storeCode;

    /** 用户号 **/
    private String openId;
}