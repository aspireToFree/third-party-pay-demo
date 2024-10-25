package com.kz.tppd.gateway.vo.smf.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API - 查询支付订单结果返回参数VO
 * @author kz
 * @date 2022/8/11 11:18
 */
@Getter
@Setter
@ToString
public class ApiQueryPayOrderResponseVO implements Serializable {

    private static final long serialVersionUID = 7867248489282522983L;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 扫码富订单号 **/
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

    /** 用户号 **/
    private String openId;
}