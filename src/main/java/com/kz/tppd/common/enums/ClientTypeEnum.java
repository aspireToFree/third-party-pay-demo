package com.kz.tppd.common.enums;

import lombok.Getter;

/**
 * 客户端类型枚举
 * @author kz
 * @date 2024/10/1 22:09
 */
@Getter
public enum ClientTypeEnum {

    //枚举值
    MINI("MINI", "小程序"),
    PUBLIC("PUBLIC", "公众号"),
    APP("APP", "APP"),
    ;

    private String code;
    private String mesg;

    ClientTypeEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}