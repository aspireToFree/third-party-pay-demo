package com.kz.tppd.gateway.plugin;

import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.service.ChannelPayDispatcherService;
import com.kz.tppd.trade.dto.request.PayOrderQueryRequestDTO;
import com.kz.tppd.trade.dto.request.RefundQueryRequestDTO;
import com.kz.tppd.trade.dto.request.RefundRequestDTO;
import com.kz.tppd.trade.dto.request.UnifiedOrderRequestDTO;
import com.kz.tppd.trade.dto.response.PayOrderQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundQueryResponseDTO;
import com.kz.tppd.trade.dto.response.RefundResponseDTO;
import com.kz.tppd.trade.dto.response.UnifiedOrderResponseDTO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 通道支付插件抽象类
 * @author kz
 * @date 2019/2/11
 */
@Slf4j
public abstract class BaseChannelPayPlugin {

    @Resource
    private ChannelPayDispatcherService channelPayDispatcherService;

    /**
     * 初始化方法，向通道池中注册通道服务插件
     */
    @PostConstruct
    void init() {
        channelPayDispatcherService.register(this);
    }

    /**
     * 获取当前通道号
     * @return 通道号
     */
    public abstract String getChannelCode();

    /**
     *  统一下单支付接口
     * @param requestDTO 请求参数
     * @return 响应结果
     */
    public UnifiedOrderResponseDTO unifiedOrder(UnifiedOrderRequestDTO requestDTO){
        //通道未提供此服务
        throw new BaseException(CommonErrorEnum.SERVICE_NOT_EXIST);
    }

    /**
     * 支付订单查询接口
     * @param requestDTO 请求参数
     * @return 响应结果
     */
    public PayOrderQueryResponseDTO orderQuery(PayOrderQueryRequestDTO requestDTO){
        //通道未提供此服务
        throw new BaseException(CommonErrorEnum.SERVICE_NOT_EXIST);
    }

    /**
     *  退款接口
     * @param requestDTO 请求参数
     * @return 响应结果
     * Created by kz on 2018/7/13 21:54.
     */
    public RefundResponseDTO refund(RefundRequestDTO requestDTO){
        //通道未提供此服务
        throw new BaseException(CommonErrorEnum.SERVICE_NOT_EXIST);
    }

    /**
     * 退款查询接口
     * @param requestDTO 请求参数
     * @return 响应结果
     * Created by kz on 2018/7/13 21:54.
     */
    public RefundQueryResponseDTO refundQuery(RefundQueryRequestDTO requestDTO){
        //通道未提供此服务
        throw new BaseException(CommonErrorEnum.SERVICE_NOT_EXIST);
    }
}