package com.kz.tppd.gateway.vo.smf.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API - 查询退款订单结果请求参数VO
 * @author kz
 * @date 2022/8/11 11:18
 */
@Getter
@Setter
@ToString
public class ApiQueryRefundOrderRequestVO implements Serializable {

    private static final long serialVersionUID = -4601246699117627392L;

    /** 扫码富订单号 **/
    private String orderNo;

    /** 合作方订单号 **/
    private String outTradeNo;
}