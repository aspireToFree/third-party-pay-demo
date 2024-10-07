package com.kz.tppd.trade.dto.response;

import com.kz.tppd.common.enums.ChannelStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单查询返回参数
 * @author kz
 * @date 2019/2/14
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PayOrderQueryResponseDTO extends BasePayResponseDTO {

    private static final long serialVersionUID = -4066613685748111967L;

    public PayOrderQueryResponseDTO(){
        super();
    }

    public PayOrderQueryResponseDTO(String respCode , String respMsg) {
        super(respCode, respMsg);
    }

    public PayOrderQueryResponseDTO(ChannelStatusEnum channelStatusEnum) {
        super(channelStatusEnum);
    }

    public PayOrderQueryResponseDTO(ChannelStatusEnum channelStatusEnum , String respCode , String respMsg) {
        super(channelStatusEnum , respCode, respMsg);
    }
}