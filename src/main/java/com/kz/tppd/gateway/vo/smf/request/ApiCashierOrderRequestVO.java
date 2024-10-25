package com.kz.tppd.gateway.vo.smf.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * API - 收银台下单请求参数VO
 * @author kz
 * @date 2022/7/1 17:39
 */
@Getter
@Setter
@ToString
public class ApiCashierOrderRequestVO implements Serializable {

    private static final long serialVersionUID = -2013924592911614357L;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 商户号 **/
    private String mercCode;

    /** 门店编号 **/
    private String storeCode;

    /** 终端号 **/
    private String termNo;

    /** 订单金额 **/
    private BigDecimal orderAmount;

    /** 客户端类型 **/
    private String clientType;

    /** 产品大类 **/
    private String proMainType;

    /** 产品能力 **/
    private String productCapability;

    /** openId **/
    private String openId;

    /** appId **/
    private String appId;

    /** 交易说明 **/
    private String tradeDesc;

    /** 后台回调地址 **/
    private String notifyUrl;

    /** 前端回调地址 **/
    private String frontNotifyUrl;

    /** 终端IP **/
    private String termIp;

    /** 失效时间 **/
    private Integer timeExpire;
}