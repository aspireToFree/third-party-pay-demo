package com.kz.tppd.common.enums;

/**
 * 支付方式枚举
 * @author kz
 * @date 2024/10/1 22:09
 */
public enum PayMethodEnum {

    //枚举值
    WECHAT_PUBLIC("WECHAT_PUBLIC", "微信公众号"),
    WECHAT_MINI("WECHAT_MINI", "微信小程序"),
    WECHAT_APP("WECHAT_APP", "微信APP"),

    ALIPAY_PUBLIC("ALIPAY_PUBLIC", "支付宝生活号"),
    ALIPAY_MINI("ALIPAY_MINI", "支付宝小程序"),
    ALIPAY_APP("ALIPAY_APP", "支付宝APP"),

    YSF_PUBLIC("YSF_PUBLIC", "云闪付公众号"),
    YSF_MINI("YSF_MINI", "云闪付小程序"),
    ;

    private String code;
    private String mesg;

    PayMethodEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }

    public String getCode() {
        return code;
    }

    public String getMesg() {
        return mesg;
    }
}