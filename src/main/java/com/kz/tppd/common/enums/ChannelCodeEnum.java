package com.kz.tppd.common.enums;

/**
 * 通道编号
 * @author kz
 */
public enum ChannelCodeEnum {

    WECHAT("WECHAT", "微信"),
    ALIPAY("ALIPAY", "支付宝"),
    SMF("SMF", "扫码富"),
    ;

    private String code;
    private String mesg;

    ChannelCodeEnum(String code, String mesg) {
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
