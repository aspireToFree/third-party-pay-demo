package com.kz.tppd.trade.process;

import com.alibaba.fastjson.JSON;
import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.gateway.service.ChannelPayDispatcherService;
import com.kz.tppd.trade.dto.request.UnifiedOrderRequestDTO;
import com.kz.tppd.trade.dto.response.UnifiedOrderResponseDTO;
import com.kz.tppd.trade.service.TradeDecisionService;
import com.kz.tppd.utils.IDCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 统一下单流程service
 * @author kz
 * @date 2019/2/14 13:52.
 */
@Slf4j
@Service
public class UnifiedOrderProcessService {

    @Resource
    private ChannelPayDispatcherService channelPayDispatcherService;

    @Resource
    private TradeDecisionService tradeDecisionService;

    /**
     * 统一下单前处理
     * @param requestDTO 请求参数
     * Created by kz on  .
     */
    private void beforeProcess(UnifiedOrderRequestDTO requestDTO) {
        //TODO 业务参数校验（字段是否必传...）
        //TODO 风控校验（交易时间、单笔限额...）
    }

    /**
     * 统一下单流程
     * @param requestDTO 统一下单请求参数DTO
     * @return 统一下单返回参数DTO
     * Created by kz on 2018/5/17 20:21
     */
    public UnifiedOrderResponseDTO executeProcess(UnifiedOrderRequestDTO requestDTO){
        //设置平台订单，可根据自己的业务规则，生成订单号
        requestDTO.setOrderNo(IDCreator.randomUUID());

        //前处理流程
        beforeProcess(requestDTO);

        //支付决策
        tradeDecisionService.payDecision(requestDTO);

        log.info("统一下单 requestDTO：{}" , JSON.toJSONString(requestDTO));

        //TODO 数据登记到支付订单表、其他业务表

        UnifiedOrderResponseDTO responseDTO;
        try{
            //通道路由 转到对应通道
            responseDTO = (UnifiedOrderResponseDTO) channelPayDispatcherService.dispatcher(requestDTO);
            if(responseDTO == null){
                //网关返回为空
                throw new BaseException(CommonErrorEnum.GATEWAY_RESPONSE_NULL);
            }
        } catch (BaseException e) {
            //业务异常
            log.error("GATEWAY统一下单异常", e);
            throw new BaseException(e.getCode() , e.getMessage() , e);
        } catch (Exception e) {
            log.error("GATEWAY统一下单异常", e);
            throw new BaseException(CommonErrorEnum.CALL_UNIFIED_ORDER_FAIL);
        }

        log.info("统一下单 responseDTO：{}" , JSON.toJSONString(responseDTO));

        if(responseDTO.isSuccess()){
            log.info("下单成功");
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁，保证幂等性），下单成功业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
        }
        else {
            log.info("下单失败");
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁），下单失败业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
            //通道错误码：responseDTO.getErrorCode()
            //通道错误描述：responseDTO.getErrorMessage()
        }
        return responseDTO;
    }
}