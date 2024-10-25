package com.kz.tppd.gateway.vo.smf.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API - 查询退款订单结果返回参数VO
 * @author kz
 * @date 2022/8/11 11:18
 */
@Getter
@Setter
@ToString
public class ApiQueryRefundOrderResponseVO implements Serializable {

    private static final long serialVersionUID = -4601246699117627393L;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 扫码富订单号 **/
    private String orderNo;

    /** 退款状态 **/
    private String refundStatus;

    /** 失败说明 **/
    private String failMessage;

    /** 退款金额 **/
    private String refundAmount;

    /** 退款手续费 **/
    private String refundFee;

    /** 退款完成时间 **/
    private String finishTime;
}