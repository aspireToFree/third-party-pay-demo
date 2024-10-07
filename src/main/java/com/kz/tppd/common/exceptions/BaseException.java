package com.kz.tppd.common.exceptions;

import com.kz.tppd.common.enums.CommonErrorEnum;

/**
 * 基础业务异常
 * Created by kz on 2018/4/24.
 */
public class BaseException extends RuntimeException {

    /** 序列化ID */
    private static final long serialVersionUID = -9177074358208860222L;

    /** 错误码 */
    private final String code;

    /** 错误信息 */
    private final String mesg;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误信息
     */
    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.mesg = message;
    }

    /**
     * 构造函数
     * @param message 错误信息
     * @param throwable 异常对象
     */
    public BaseException(String message, Throwable throwable) {
        super(message, throwable);
        this.code = CommonErrorEnum.SYSTEM_ERROR.getCode();
        this.mesg = message;
    }

    /**
     * 构造函数
     * @param message 错误信息
     * @param throwable 异常对象
     */
    public BaseException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.mesg = message;
    }

    /**
     * 构造函数
     * @param errorEnum 错误枚举
     */
    public BaseException(CommonErrorEnum errorEnum) {
        super(errorEnum.getMesg());
        this.code = errorEnum.getCode();
        this.mesg = errorEnum.getMesg();
    }

    /**
     * 构造函数
     * @param errorEnum 错误枚举
     * @param throwable 异常对象
     */
    public BaseException(CommonErrorEnum errorEnum, Throwable throwable) {
        super(errorEnum.getMesg(), throwable);
        this.code = errorEnum.getCode();
        this.mesg = errorEnum.getMesg();
    }

    public String getCode() {
        return code;
    }

    public String getMesg() {
        return mesg;
    }
}
