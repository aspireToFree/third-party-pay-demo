package com.kz.tppd.trade.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * pay退款请求参数
 * Created by kz on 2018/5/19 22:24.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RefundRequestDTO extends BasePayRequestDTO {

    private static final long serialVersionUID = 2989208942956315270L;

    /** 原支付平台订单号 **/
    private String originalOrderNo;

    /** 原通道订单号 **/
    private String originalChannelOrderNo;

    /** 原交易时间 **/
    private Date originalTradeTime;

    /** 退款原因 **/
    private String refundReason;

    /** 退款金额 **/
    private BigDecimal refundAmount;
}