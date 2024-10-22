package com.kz.tppd.trade.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * pay退款查询请求参数
 * Created by kz on 2018/5/19 22:24.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RefundQueryRequestDTO extends BasePayRequestDTO {

    private static final long serialVersionUID = 1597152307754163119L;

    /** 通道订单号 **/
    private String channelOrderNo;

    /** 原支付平台订单号 **/
    private String originalOrderNo;

    /** 原通道订单号 **/
    private String originalChannelOrderNo;
}