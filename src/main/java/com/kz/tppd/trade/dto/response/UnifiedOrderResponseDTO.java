package com.kz.tppd.trade.dto.response;

import com.kz.tppd.common.enums.ChannelStatusEnum;
import lombok.*;

/**
 * 统一下单返回参数
 * @author kz
 * @date 2019/2/14
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UnifiedOrderResponseDTO extends BasePayResponseDTO {

    private static final long serialVersionUID = -1144375235341716302L;

    /** 支付地址 **/
    private String payUrl;

    /** 附加数据 **/
    private String attach;


    public UnifiedOrderResponseDTO(){
        super();
    }

    public UnifiedOrderResponseDTO(String respCode , String respMsg){
        super(respCode , respMsg);
    }

    public UnifiedOrderResponseDTO(ChannelStatusEnum channelStatusEnum){
        super(channelStatusEnum);
    }

    public UnifiedOrderResponseDTO(ChannelStatusEnum channelStatusEnum , String errorMessage){
        super(channelStatusEnum , errorMessage);
    }

    public UnifiedOrderResponseDTO(ChannelStatusEnum channelStatusEnum , String errorCode , String errorMessage){
        super(channelStatusEnum , errorCode , errorMessage);
    }
}