package com.kz.tppd.gateway.vo.smf.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * API - 报文主体 返回VO
 * Created by kz on 2018/5/16 10:46.
 */
@Getter
@Setter
@ToString
public class ApiMainResponseVO implements Serializable {

    private static final long serialVersionUID = -498114570214056860L;

    /** 签名 部分失败交易没有签名**/
    private String sign;

    /** 随机字符串 **/
    private String nonce;

    /** 响应码 **/
    private String respCode;

    /** 响应描述 **/
    private String respMsg;

    /** 响应数据 **/
    private String respData;

    public ApiMainResponseVO() {
    }

    public ApiMainResponseVO(String respCode, String respMsg) {
        this.setNonce(System.currentTimeMillis()+"");
        this.setRespCode(respCode);
        this.setRespMsg(respMsg);
    }

    /**
     * 构建待签字符串
     * @param key key
     * @return 待签字符串
     */
    public String getSignData(String key) {
        SortedMap<String, String> fields = new TreeMap<String, String>();
        fields.put("nonce", getNonce());
        fields.put("respCode", getRespCode());
        fields.put("respMsg", getRespMsg());
        fields.put("respData", getRespData());

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

    /**
     * 判断响应码是否成功
     * @return  true：成功，false：失败
     * Created by kz on 2018/5/17 21:00.
     */
    public boolean isSuccess(){
        return StringUtils.equals(respCode , "0000");
    }
}