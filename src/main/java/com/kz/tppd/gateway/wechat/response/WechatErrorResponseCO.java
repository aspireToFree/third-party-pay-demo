package com.kz.tppd.gateway.wechat.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 微信错误返回参数CO
 * @author kz
 * @date 2022/5/10 14:00
 */
@Getter
@Setter
@ToString
public class WechatErrorResponseCO {

    /** code **/
    private String code;

    /** message **/
    private String message;
}
