package com.kz.tppd.gateway.service;

import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.plugin.BaseChannelPayPlugin;
import com.kz.tppd.trade.dto.request.*;
import com.kz.tppd.trade.dto.response.BasePayResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道支付路由器
 * @author kz
 * @date 2019/2/11
 */
@Slf4j
@Component
public class ChannelPayDispatcherService {

    /** 通道池 */
    private final Map<String, BaseChannelPayPlugin> channelPool = new ConcurrentHashMap<>();

    /**
     * 通道插件注册至通道路由器
     * @param baseChannelPayPlugin 通道插件
     */
    public void register(BaseChannelPayPlugin baseChannelPayPlugin) {
        channelPool.put(baseChannelPayPlugin.getChannelCode(), baseChannelPayPlugin);
        log.info("向支付路由器注册通道[{}]", baseChannelPayPlugin.getChannelCode());
    }

    /**
     * 根据请求参数进行分发
     * @param requestDTO 请求参数
     * @return 响应结果
     */
    public BasePayResponseDTO dispatcher(BasePayRequestDTO requestDTO) {
        String channelCode = requestDTO.getChannelCode();
        if (StringUtils.isBlank(channelCode)) {
            //合作机构号为空
            throw new BaseException(CommonErrorEnum.CHANNEL_CODE_IS_NULL);
        }
        log.info("从通道池中获取[channelCode={}]的通道组件...", channelCode);
        BaseChannelPayPlugin baseChannelPayPlugin = channelPool.get(channelCode);
        if (baseChannelPayPlugin == null) {
            //找不到通道插件
            throw new BaseException(CommonErrorEnum.CHANNEL_NOT_EXIST);
        }

        Class<? extends BasePayRequestDTO> service = requestDTO.getClass();

        /*服务分发*/

        //统一下单
        if (service == UnifiedOrderRequestDTO.class) {
            return baseChannelPayPlugin.unifiedOrder((UnifiedOrderRequestDTO) requestDTO);
        }
        //支付订单查询
        else if (service == PayOrderQueryRequestDTO.class) {
            return baseChannelPayPlugin.orderQuery((PayOrderQueryRequestDTO) requestDTO);
        }
        //退款接口
        else if (service == RefundRequestDTO.class) {
            return baseChannelPayPlugin.refund((RefundRequestDTO) requestDTO);
        }
        //退款查询接口
        else if (service == RefundQueryRequestDTO.class) {
            return baseChannelPayPlugin.refundQuery((RefundQueryRequestDTO) requestDTO);
        }

        //通道未提供此服务
        throw new BaseException(CommonErrorEnum.SERVICE_NOT_EXIST);
    }
}
