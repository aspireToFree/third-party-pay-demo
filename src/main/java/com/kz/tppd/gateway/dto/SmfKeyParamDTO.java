package com.kz.tppd.gateway.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 扫码富密钥参数DTO
 * @author kz
 * @date 2024/10/23 11:25
 */
@Getter
@Setter
@ToString
public class SmfKeyParamDTO implements Serializable {

    /** 平台商户号 **/
    private String platformMercCode;

    /** 代理商编号 **/
    private String agentCode;

    /** 签名密钥 **/
    private String signKey;

    /** 报文密钥 **/
    private String msgKey;
}