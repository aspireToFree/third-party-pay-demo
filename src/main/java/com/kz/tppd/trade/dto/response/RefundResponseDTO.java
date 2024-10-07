package com.kz.tppd.trade.dto.response;

import com.kz.tppd.common.enums.ChannelStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * pay交易查询返回参数
 * @author kz
 * @date 2019/2/14
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RefundResponseDTO extends BasePayResponseDTO {

    private static final long serialVersionUID = -2128314537110747333L;

    /**
     * 构造函数
     */
    public RefundResponseDTO() {
        super();
    }

    public RefundResponseDTO(String respCode , String respMsg) {
        super(respCode, respMsg);
    }

    public RefundResponseDTO(ChannelStatusEnum channelStatusEnum) {
        super(channelStatusEnum);
    }

    public RefundResponseDTO(ChannelStatusEnum channelStatusEnum , String respCode , String respMsg) {
        super(channelStatusEnum , respCode, respMsg);
    }
}