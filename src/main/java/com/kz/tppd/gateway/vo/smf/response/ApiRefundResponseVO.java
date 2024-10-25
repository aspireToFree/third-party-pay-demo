package com.kz.tppd.gateway.vo.smf.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API - 退款返回参数VO
 * @author kz
 * @date 2022/8/11 11:18
 */
@Getter
@Setter
@ToString
public class ApiRefundResponseVO implements Serializable {

    private static final long serialVersionUID = 2155281398488694177L;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 扫码富订单号 **/
    private String orderNo;

    /** 退款状态 **/
    private String refundStatus;

    /** 失败说明 **/
    private String failMessage;
}