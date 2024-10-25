package com.kz.tppd.trade.service;

import com.kz.tppd.common.enums.ChannelCodeEnum;
import com.kz.tppd.common.enums.PayMethodEnum;
import com.kz.tppd.trade.dto.request.BasePayRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 交易决策
 * @author kz
 * @date 2024/10/7 11:27
 */
@Slf4j
@Service
public class TradeDecisionService {

    /**
     * 支付决策
     * @param  requestDTO requestDTO
     * Created by kz on 2018/6/19 10:34.
     */
    public void payDecision(BasePayRequestDTO requestDTO) {
        //根据路由规则，选择要走的通道
        routeChannel(requestDTO);

        //TODO 获取通道的密钥参数（demo，暂时都写死在yml配置文件中，实际业务可从配置表获取）
        //...
    }

    /**
     * 订单查询决策
     * @param  requestDTO requestDTO
     * Created by kz on 2018/6/19 10:34.
     */
    public void orderQueryDecision(BasePayRequestDTO requestDTO) {
        //TODO 获取通道的密钥参数（demo，暂时都写死在yml配置文件中，实际业务可从配置表获取）
        //...

        //实际业务代码，不用路由通道，因为demo订单没有存表，得通过路由来选择通道
        routeChannel(requestDTO);
    }

    /**
     * 路由通道
     * @param requestDTO 请求参数DTO
     * Created by kz on 2024/10/7 11:34.
     */
    private void routeChannel(BasePayRequestDTO requestDTO) {
        //已指定通道
        if(StringUtils.isNotBlank(requestDTO.getChannelCode())){
            return;
        }

        //未指定通道（demo就根据支付方式写死通道，实际业务可根据路由配置来路由要走的通道）
        //支付宝走支付宝直联通道
        if(PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum()) ||
                PayMethodEnum.ALIPAY_PUBLIC.equals(requestDTO.getPayMethodEnum()) ||
                PayMethodEnum.ALIPAY_APP.equals(requestDTO.getPayMethodEnum())){
            log.info("支付宝的支付方式写死[ALIPAY]的通道，实际业务可根据路由配置来路由通道");
            requestDTO.setChannelCode(ChannelCodeEnum.ALIPAY.getCode());
        }
        //微信走微信直联通道
        else if(PayMethodEnum.WECHAT_PUBLIC.equals(requestDTO.getPayMethodEnum()) ||
                PayMethodEnum.WECHAT_MINI.equals(requestDTO.getPayMethodEnum()) ||
                PayMethodEnum.WECHAT_APP.equals(requestDTO.getPayMethodEnum())){
            log.info("微信的支付方式写死[WECHAT]的通道，实际业务可根据路由配置来路由通道");
            requestDTO.setChannelCode(ChannelCodeEnum.WECHAT.getCode());
        }
        //云闪付走扫码富间联通道
        else if(PayMethodEnum.YSF_PUBLIC.equals(requestDTO.getPayMethodEnum())){
            log.info("云闪付的支付方式写死[SMF]的通道，实际业务可根据路由配置来路由通道");
            requestDTO.setChannelCode(ChannelCodeEnum.SMF.getCode());
        }

        log.info("路由到[{}]通道" , requestDTO.getChannelCode());
    }
}