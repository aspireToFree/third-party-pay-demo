package com.kz.tppd.trade.service;

import com.kz.tppd.trade.dto.response.BasePayResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单结果处理类
 * @author kz
 * @date 2019/2/14 14:52.
 */
@Slf4j
@Service
public class OrderResultService {

    /**
     * 支付订单结果处理
     * @param responseDTO 返回结果DTO
     * Created by kz on 2018/6/22 19:48.
     */
    public void payOrderResultProcess(BasePayResponseDTO responseDTO) {
        //TODO 校验支付订单状态，看状态是否已经变更

        //处理中或未支付，跳过不处理
        if(responseDTO.isProcessing() || responseDTO.isNotPay()){
            //TODO 变更下次定时查询时间
            return;
        }

        //支付成功
        if(responseDTO.isSuccess()){
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁，保证幂等性），支付成功业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
        }
        //支付失败
        else {
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁），支付失败业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
            //通道错误码：responseDTO.getErrorCode()
            //通道错误描述：responseDTO.getErrorMessage()
        }
    }

    /**
     * 退款订单结果处理
     * @param responseDTO 返回结果DTO
     * Created by kz on 2018/6/22 19:48.
     */
    public void refundOrderResultProcess(BasePayResponseDTO responseDTO) {
        //TODO 校验支付订单状态，看状态是否已经变更

        //处理中，跳过不处理
        if(responseDTO.isProcessing()){
            //TODO 变更下次定时查询时间
            return;
        }

        //退款成功
        if(responseDTO.isSuccess()){
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁，保证幂等性），退款成功业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
        }
        //退款失败
        else {
            //TODO 变更支付订单状态（建议：update将订单状态作为查询条件，作为乐观锁），退款失败业务处理
            //通道订单号 responseDTO.getChannelOrderNo()
            //通道错误码：responseDTO.getErrorCode()
            //通道错误描述：responseDTO.getErrorMessage()
        }
    }
}