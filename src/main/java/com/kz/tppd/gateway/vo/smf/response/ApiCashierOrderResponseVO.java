package com.kz.tppd.gateway.vo.smf.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API - 收银台下单返回参数VO
 * @author kz
 * @date 2022/7/1 17:39
 */
@Getter
@Setter
@ToString
public class ApiCashierOrderResponseVO implements Serializable {

    private static final long serialVersionUID = -2013924592911614358L;

    /** 扫码富订单号 **/
    private String orderNo;

    /** 合作方订单号 **/
    private String outTradeNo;

    /** 支付金额 **/
    private String payAmount;

    /** 订单金额 **/
    private String orderAmount;

    /** 优惠金额 **/
    private String discountAmount;

    /** 支付地址 **/
    private String payUrl;

    /** 附加参数 **/
    private String attach;
}