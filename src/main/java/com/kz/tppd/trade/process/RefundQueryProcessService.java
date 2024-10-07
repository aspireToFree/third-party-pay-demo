package com.kz.tppd.trade.process;

import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.service.ChannelPayDispatcherService;
import com.kz.tppd.trade.dto.request.RefundQueryRequestDTO;
import com.kz.tppd.trade.dto.response.RefundQueryResponseDTO;
import com.kz.tppd.trade.service.OrderResultService;
import com.kz.tppd.trade.service.TradeDecisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 退款查询流程service
 * @author kz
 * @date 2019/3/12
 */
@Slf4j
@Service
public class RefundQueryProcessService {

    @Resource
    private ChannelPayDispatcherService channelPayDispatcherService;

    @Resource
    private TradeDecisionService tradeDecisionService;

    @Resource
    private OrderResultService orderResultProcess;

    /**
     * 退款查询流程
     * @param requestDTO 请求参数
     * @return 返回参数
     * Created by kz on 2018/5/17 20:21.
     */
    public RefundQueryResponseDTO executeProcess(RefundQueryRequestDTO requestDTO){
        //TODO 查询订单是否存在、是否为退款订单

        //TODO 校验订单状态，是否成功、失败

        //TODO 从支付订单获取通道编号、通道商户号，使用跟支付订单一样的通道与通道商户号
        //requestDTO.setChannelCode();

        //订单查询决策
        tradeDecisionService.orderQueryDecision(requestDTO);

        log.info("退款订单查询 requestDTO：{}" , requestDTO);

        RefundQueryResponseDTO responseDTO;
        try{
            //通道路由 转到对应通道
            responseDTO = (RefundQueryResponseDTO)channelPayDispatcherService.dispatcher(requestDTO);
            if(responseDTO == null){
                //网关返回为空
                throw new BaseException(CommonErrorEnum.GATEWAY_RESPONSE_NULL);
            }
        } catch (BaseException e) {
            //业务异常
            log.error("GATEWAY退款订单查询异常", e);
            throw new BaseException(e.getCode() , e.getMessage() , e);
        } catch (Exception e) {
            log.error("GATEWAY退款订单查询异常", e);
            throw new BaseException(CommonErrorEnum.CALL_REFUND_QUERY_FAIL);
        }
        log.info("退款订单查询 responseDTO：{}" , responseDTO);

        //退款订单结果公共处理
        orderResultProcess.refundOrderResultProcess(responseDTO);
        return responseDTO;
    }
}