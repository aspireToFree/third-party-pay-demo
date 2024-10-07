package com.kz.tppd.common.enums;

/**
 * common异常枚举
 * @author kz
 */
public enum CommonErrorEnum{
    //枚举值
    SUCCESS("0000" , "成功"),

    SYSTEM_ERROR("CMM9999", "系统繁忙，请稍后重试"),

    FILED_IS_NULL("CMM0001", "[%s]为空"),

    BUSINESS_ERROR("CMM0002", "业务异常[%s]"),

    DES_ENCRYPT_ERROR("CMM1001", "des加密失败"),

    DES_DECRYPT_ERROR("CMM1002", "des解密失败"),

    FIELD_IS_EMPTY("CMM1003", "%s为空"),

    ENCRYPT_ERROR("CMM1004", "非对称加/解密错误"),

    FILE_NOT_EXSIT("CMM1005", "文件[%s]不存在"),

    READ_FILE_ERROR("CMM1006", "读文件[%s]出错"),

    CREATE_DIR_FAIL("CMM1007", "创建目录[%s]失败"),

    WRITE_FILE_ERROR("CMM1008", "写文件[%s]出错"),

    HTTP_REQUEST_ERROR("CMM1009", "HTTP请求异常"),

    HMACSHA1_ENCRYPT_ERROR("CMM1010", "HMACSHA1加密失败"),

    CREATE_QR_CODE("CMM1010", "HMACSHA1加密失败"),

    AES_DECRYPT_ERROR("CMM1011", "AES解密失败"),

    CHANNEL_CODE_IS_NULL("GATEWAY2016", "通道编号为空"),

    CHANNEL_NOT_EXIST("GATEWAY2001", "找不到通道插件"),

    SERVICE_NOT_EXIST("GATEWAY2002", "通道未提供此服务"),

    CHANNEL_ERROR("GATEWAY2014", "上游返回结果异常"),

    CHANNEL_FAIL("GATEWAY2015", "上游失败，%s"),

    GATEWAY_RESPONSE_NULL("GATEWAY2015", "网关返回为空"),

    CALL_UNIFIED_ORDER_FAIL("PAY2010", "请求统一下单服务失败"),
    CALL_PAY_ORDER_QUERY_FAIL("PAY2009", "请求支付订单查询服务失败"),
    CALL_REFUND_FAIL("PAY2009", "请求退款服务失败"),
    CALL_REFUND_QUERY_FAIL("PAY2009", "请求退款订单查询服务失败"),
    ;


    private String code;

    private String mesg;

    CommonErrorEnum(String code, String mesg) {
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
