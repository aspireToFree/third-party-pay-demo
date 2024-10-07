package com.kz.tppd.common.enums;

/**
 * 通道状态枚举
 * @author kz
 * @date 2024/9/24 11:48
 */
public enum ChannelStatusEnum {

    SUCCESS("SUCCESS", "成功"),
    FAIL("FAIL", "失败"),
    PROCESSING("PROCESSING", "处理中"),
    NOTPAY("NOTPAY", "未支付"),
    REQUEST_SUCCESS("REQUEST_SUCCESS", "请求成功"),
    ;

    private String code;
    private String mesg;

    ChannelStatusEnum(String code, String mesg) {
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