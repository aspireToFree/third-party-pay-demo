package com.kz.tppd.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * ID的工具类
 * Created by kz on 2017/12/20.
 */
@Slf4j
public class IDCreator {

    /**
     * 生成随机32位的UUID
     * @return UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
