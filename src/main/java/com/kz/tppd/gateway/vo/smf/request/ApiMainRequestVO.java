package com.kz.tppd.gateway.vo.smf.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * API - 报文主体 请求VO
 * Created by kz on 2018/5/19 17:03.
 */
@Getter
@Setter
@ToString
public class ApiMainRequestVO implements Serializable {

    private static final long serialVersionUID = -5502550777950437345L;

    /** 编码方式 **/
    private String charset;

    /** 版本号 **/
    private String version;

    /** 签名算法 **/
    private String signType;

    /** 加密算法 **/
    private String encType;

    /** 平台商户号 **/
    private String platformMercCode;

    /** 代理商编号 **/
    private String agentCode;

    /** 签名 **/
    private String sign;

    /** 随机串 **/
    private String nonce;

    /** 请求报文体 **/
    private String reqData;

    /**
     * 构建待签字符串
     * @param key key
     * @return 待签字符串
     */
    public String getSignData(String key) {
        SortedMap<String, String> fields = new TreeMap<String, String>();
        fields.put("version", getVersion());
        fields.put("charset", getCharset());
        fields.put("signType", getSignType());
        fields.put("encType", getEncType());

        if(StringUtils.isNotBlank(platformMercCode)){
            fields.put("platformMercCode", getPlatformMercCode());
        } else if(StringUtils.isNotBlank(agentCode)){
            fields.put("agentCode", getAgentCode());
        }

        fields.put("nonce", getNonce());
        fields.put("reqData", getReqData());

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                builder.append(entry.getKey()).append("=");
                builder.append(entry.getValue()).append("&");
            }
        }
        builder.append("key=").append(key);
        return builder.toString();
    }
}