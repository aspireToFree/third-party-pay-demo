package com.kz.tppd.trade.dto.response;

import com.kz.tppd.common.enums.ChannelStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * pay退款查询返回参数
 * @author kz
 * @date 2019/2/14
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RefundQueryResponseDTO extends BasePayResponseDTO {

    private static final long serialVersionUID = -6604773618813269423L;

    /**
     * 构造函数
     */
    public RefundQueryResponseDTO() {
        super();
    }

    /**
     * 构造函数
     * @param respCode 返回码
     * @param respMsg 返回信息
     */
    public RefundQueryResponseDTO(String respCode , String respMsg) {
        super(respCode, respMsg);
    }

    public RefundQueryResponseDTO(ChannelStatusEnum channelStatusEnum) {
        super(channelStatusEnum);
    }

    public RefundQueryResponseDTO(ChannelStatusEnum channelStatusEnum , String respCode , String respMsg) {
        super(channelStatusEnum , respCode, respMsg);
    }
}