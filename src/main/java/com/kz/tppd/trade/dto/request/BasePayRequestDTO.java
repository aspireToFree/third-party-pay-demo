package com.kz.tppd.trade.dto.request;

import com.kz.tppd.common.enums.PayMethodEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * pay公共请求参数
 * @author kz
 * @date 2019/2/12
 */
@Getter
@Setter
@ToString
public abstract class BasePayRequestDTO implements Serializable {

    private static final long serialVersionUID = 407555010492104047L;

    /** 平台订单号 **/
    private String orderNo;

    /**  客户端IP **/
    private String clientIp;

    /**  交易请求时间 **/
    private Date requestTime;

    /** 通道编号 **/
    private String channelCode;

    /** 支付方式枚举 **/
    private PayMethodEnum payMethodEnum;
}