package com.kz.tppd.trade.process;

import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.service.ChannelPayDispatcherService;
import com.kz.tppd.trade.dto.request.RefundRequestDTO;
import com.kz.tppd.trade.dto.response.RefundResponseDTO;
import com.kz.tppd.trade.service.TradeDecisionService;
import com.kz.tppd.utils.IDCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 退款流程service
 * @author kz
 * @date 2019/3/12
 */
@Slf4j
@Service
public class RefundProcessService {

    @Resource
    private ChannelPayDispatcherService channelPayDispatcherService;

    @Resource
    private TradeDecisionService tradeDecisionService;

    /**
     * 退款流程
     * @param requestDTO 请求参数DTO
     * @return 返回参数DTO
     * Created by kz on 2021/11/8 10:49.
     */
    private RefundResponseDTO executeProcess(RefundRequestDTO requestDTO){
        //TODO 业务参数校验（字段是否必传...）
        //TODO 校验订单能否退款，支付订单是否支付，支付订单能退款的金额。。。

        //设置平台订单，可根据自己的业务规则，生成订单号
        requestDTO.setOrderNo(IDCreator.randomUUID());

        //TODO 设置支付平台订单号、支付通道订单号
        //requestDTO.setOriginalOrderNo(xx);
        //requestDTO.setOriginalChannelOrderNo(xx);


        //TODO 从支付订单获取通道编号、通道商户号，使用跟支付订单一样的通道与通道商户号
        //requestDTO.setChannelCode();

        //订单查询决策
        tradeDecisionService.orderQueryDecision(requestDTO);

        log.info("退款 requestDTO：{}" , requestDTO);

        //TODO 数据登记到支付订单表、其他业务表

        RefundResponseDTO responseDTO;
        try{
            //通道路由 转到对应通道
            responseDTO = (RefundResponseDTO)channelPayDispatcherService.dispatcher(requestDTO);
            if(responseDTO == null){
                //网关返回为空
                throw new BaseException(CommonErrorEnum.GATEWAY_RESPONSE_NULL);
            }
        } catch (BaseException e) {
            //业务异常
            log.error("GATEWAY退款异常", e);
            throw new BaseException(e.getCode() , e.getMessage() , e);
        } catch (Exception e) {
            log.error("GATEWAY退款异常", e);
            throw new BaseException(CommonErrorEnum.CALL_REFUND_FAIL);
        }
        log.info("退款 responseDTO：{}" , responseDTO);

        //退款请求成功
        if(responseDTO.isSuccess()) {
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁，保证幂等性），退款请求成功业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
        }
        //退款请求失败
        else {
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁），退款请求失败业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
            //通道错误码：responseDTO.getErrorCode()
            //通道错误描述：responseDTO.getErrorMessage()
        }
        return responseDTO;
    }
}