package com.kz.tppd.common.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 * @author kz
 * @date 2024/10/1 22:09
 */
@Getter
public enum PayMethodEnum {

    //枚举值
    WECHAT_PUBLIC("WECHAT_PUBLIC", "微信公众号" , ClientTypeEnum.PUBLIC , SmfProductCapabilityEnum.WECHAT_JS_PAY),
    WECHAT_MINI("WECHAT_MINI", "微信小程序" , ClientTypeEnum.MINI , SmfProductCapabilityEnum.WECHAT_JS_PAY),
    WECHAT_APP("WECHAT_APP", "微信APP" , ClientTypeEnum.APP , null),

    ALIPAY_PUBLIC("ALIPAY_PUBLIC", "支付宝生活号" , ClientTypeEnum.PUBLIC , SmfProductCapabilityEnum.ALIPAY_JS_PAY),
    ALIPAY_MINI("ALIPAY_MINI", "支付宝小程序" , ClientTypeEnum.MINI , SmfProductCapabilityEnum.ALIPAY_JS_PAY),
    ALIPAY_APP("ALIPAY_APP", "支付宝APP" , ClientTypeEnum.APP, null),

    YSF_PUBLIC("YSF_PUBLIC", "云闪付公众号" , ClientTypeEnum.PUBLIC , SmfProductCapabilityEnum.YSF_JS_PAY),
    ;

    private String code;
    private String mesg;

    //客户端类型
    private ClientTypeEnum clientTypeEnum;

    //扫码富产品能力枚举
    private SmfProductCapabilityEnum smfProductCapabilityEnum;

    PayMethodEnum(String code, String mesg , ClientTypeEnum clientTypeEnum , SmfProductCapabilityEnum smfProductCapabilityEnum) {
        this.code = code;
        this.mesg = mesg;
        this.clientTypeEnum = clientTypeEnum;
        this.smfProductCapabilityEnum = smfProductCapabilityEnum;
    }
}