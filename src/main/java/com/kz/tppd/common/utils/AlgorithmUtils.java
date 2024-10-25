
package com.kz.tppd.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 算法工具类
 * Created by kz on 2018/11/28 9:56.
 */
public class AlgorithmUtils {

    /**
    *  计算SHA256摘要值
    * @param  data 被摘要数据
    * @return  SHA-256摘要的16进制表示
    * Created by kz on 2018/11/28 9:59.
    */
    public static String getSHA256Hex(String data){
        if(StringUtils.isBlank(data)){
            throw new IllegalArgumentException("algorithmEnum cannot be empty");
        }
        return DigestUtil.sha256Hex(data);
    }

    /**
     *  des加密，使用UTF-8编码
     * @param data 要加密的字符串
     * @param key 加密的密钥
     * @return 加密后的Base64
     * Created by kz on 2018/11/28 13:14.
     */
    public static String desEncryptBase64(String data , String key){
        if(StringUtils.isBlank(data)){
            throw new IllegalArgumentException("data cannot be empty");
        } if(StringUtils.isBlank(key)){
            throw new IllegalArgumentException("key cannot be empty");
        }
        if(key.length() % 8 != 0){
            throw new IllegalArgumentException("key的长度必须为8的倍长");
        }

        return SecureUtil.des(key.getBytes()).encryptBase64(data);
    }


    /**
     *  des解密base64表示的字符串，默认UTF-8编码
     * @param data 被加密的字符串
     * @param key 加密的密钥
     * @return 解密后的String
     * Created by kz on 2018/11/28 13:14.
     */
    public static String desDecryptFromBase64(String data , String key){
        if(StringUtils.isBlank(data)){
            throw new IllegalArgumentException("data cannot be empty");
        } if(StringUtils.isBlank(key)){
            throw new IllegalArgumentException("key cannot be empty");
        }
        if(key.length() % 8 != 0){
            throw new IllegalArgumentException("key的长度必须为8的倍长");
        }
        return SecureUtil.des(key.getBytes()).decryptStr(Base64.decode(data));
    }
}