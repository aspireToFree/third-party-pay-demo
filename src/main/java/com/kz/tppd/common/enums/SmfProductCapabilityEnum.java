package com.kz.tppd.common.enums;

import lombok.Getter;

/**
 * 扫码富产品能力枚举
 * @author kz
 * @date 2024/10/23 13:57
 */
@Getter
public enum SmfProductCapabilityEnum {

    //枚举值
    WECHAT_JS_PAY("WECHAT_JS_PAY", "微信支付"),
    ALIPAY_JS_PAY("ALIPAY_JS_PAY", "支付宝支付"),
    YSF_JS_PAY("YSF_JS_PAY", "云闪付支付"),
    ;

    private String code;
    private String mesg;

    SmfProductCapabilityEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}