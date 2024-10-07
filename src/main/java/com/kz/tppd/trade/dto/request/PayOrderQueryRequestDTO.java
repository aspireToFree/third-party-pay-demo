package com.kz.tppd.trade.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单查询请求参数
 * Created by kz on 2018/6/20 9:56.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PayOrderQueryRequestDTO extends BasePayRequestDTO {

    private static final long serialVersionUID = 7237198914374344424L;

    /** 通道订单号 **/
    private String channelOrderNo;
}