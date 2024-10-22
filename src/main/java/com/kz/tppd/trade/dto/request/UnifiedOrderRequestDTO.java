package com.kz.tppd.trade.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 统一下单请求参数
 * Created by kz on 2018/6/15 14:12.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UnifiedOrderRequestDTO extends BasePayRequestDTO {

    private static final long serialVersionUID = -1231673879653150825L;

    /** 买家支付宝用户ID **/
    private String buyerId;

    /** openId（微信openId 或 买家支付宝用户唯一标识） **/
    private String openId;

    /** appId **/
    private String appId;

    /** 支付金额 **/
    private BigDecimal payAmount;

    /** 交易描述 **/
    private String tradeDesc;
}