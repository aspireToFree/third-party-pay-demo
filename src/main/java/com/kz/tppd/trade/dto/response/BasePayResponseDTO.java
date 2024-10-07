package com.kz.tppd.trade.dto.response;

import com.kz.tppd.common.enums.ChannelStatusEnum;
import lombok.*;

import java.io.Serializable;

/**
 * pay公共返回参数
 * @author kz
 * @date 2019/2/14
 */
@Getter
@Setter
@ToString
public class BasePayResponseDTO implements Serializable {

    private static final long serialVersionUID = -3785255799392101328L;

    /** 通道订单号 **/
    private String channelOrderNo;

    /** 平台订单号 **/
    private String orderNo;

    /** 通道状态枚举 **/
    private ChannelStatusEnum channelStatusEnum;

    /** 错误码 **/
    private String errorCode;

    /** 错误描述 **/
    private String errorMessage;

    /**
    * 判断响应码是否成功
    * @return  true：成功，false：失败
    * Created by kz on 2018/5/17 21:00.
    */
    public boolean isSuccess(){
        return ChannelStatusEnum.SUCCESS.equals(this.channelStatusEnum);
    }

    /**
     * 判断响应码是否为未支付
     * @return  true：未支付，false：不是未支付
     * Created by kz on 2018/5/17 21:00.
     */
    public boolean isNotPay(){
        return ChannelStatusEnum.NOTPAY.equals(this.channelStatusEnum);
    }

    /**
     * 判断响应码是否为处理中
     * @return  true：处理中，false：不是处理中
     * Created by kz on 2018/5/17 21:00.
     */
    public boolean isProcessing(){
        return ChannelStatusEnum.PROCESSING.equals(this.channelStatusEnum);
    }

    /**
     * 判断响应码是否为请求成功
     * @return true：请求成功，false：非请求成功
     * Created by kz on 2019/4/4 14:21.
     */
    public boolean isRequestSuccess(){
        return ChannelStatusEnum.REQUEST_SUCCESS.equals(this.channelStatusEnum);
    }

    /**
     * 判断响应码是否为失败
     * @return true：失败，false：非失败
     * Created by kz on 2019/4/4 14:21.
     */
    public boolean isFail(){
        return ChannelStatusEnum.FAIL.equals(this.channelStatusEnum);
    }


    public BasePayResponseDTO(){
        this.channelStatusEnum = ChannelStatusEnum.SUCCESS;
    }

    public BasePayResponseDTO(ChannelStatusEnum channelStatusEnum){
        this.channelStatusEnum = channelStatusEnum;
    }

    public BasePayResponseDTO(String errorCode , String errorMessage){
        this.channelStatusEnum = ChannelStatusEnum.FAIL;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BasePayResponseDTO(ChannelStatusEnum channelStatusEnum , String errorCode , String errorMessage){
        this.channelStatusEnum = channelStatusEnum;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BasePayResponseDTO(ChannelStatusEnum channelStatusEnum , String errorMessage){
        this.channelStatusEnum = channelStatusEnum;
        this.errorMessage = errorMessage;
    }
}