package com.kz.tppd.gateway.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kz.tppd.common.enums.CommonErrorEnum;
import com.kz.tppd.common.exceptions.BaseException;
import com.kz.tppd.common.utils.AlgorithmUtils;
import com.kz.tppd.common.utils.HttpUtils;
import com.kz.tppd.gateway.dto.SmfKeyParamDTO;
import com.kz.tppd.gateway.vo.smf.request.ApiMainRequestVO;
import com.kz.tppd.gateway.vo.smf.response.ApiMainResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 扫码富工具类
 * @author kz
 * @date 2022/8/15 18:42
 */
@Slf4j
public class SmfUtil {

    /** 默认字符集 */
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    public static ApiMainResponseVO sendChannel(String apiMesg , String url , String reqBody , SmfKeyParamDTO smfKeyParamDTO){
        ApiMainRequestVO apiRequestVO = new ApiMainRequestVO();
        apiRequestVO.setNonce(System.currentTimeMillis()+"");
        apiRequestVO.setReqData(reqBody);

        //平台商户接入
        if(StringUtils.isNotBlank(smfKeyParamDTO.getPlatformMercCode())){
            apiRequestVO.setPlatformMercCode(smfKeyParamDTO.getPlatformMercCode());
        }
        //代理商接入
        else if(StringUtils.isNotBlank(smfKeyParamDTO.getAgentCode())){
            apiRequestVO.setPlatformMercCode(smfKeyParamDTO.getAgentCode());
        }

        String msgKey = smfKeyParamDTO.getMsgKey();
        String signKey = smfKeyParamDTO.getSignKey();

        apiRequestVO.setSign(AlgorithmUtils.getSHA256Hex(apiRequestVO.getSignData(signKey)).toUpperCase());

        apiRequestVO.setReqData(AlgorithmUtils.desEncryptBase64(reqBody , msgKey));

        String label = "扫码富-"+apiMesg+"接口";

        log.info(label + " 请求参数明文:{}", reqBody);

        String reqData = JSON.toJSONString(apiRequestVO);
        log.info(label + " 请求地址:{},请求参数:{}", url , reqData);
        String httpReponse = HttpUtils.post(reqData , url , DEFAULT_CHARSET, APPLICATION_JSON_UTF8_VALUE);
        log.info(label + " 返回参数:{}", httpReponse);

        if(StringUtils.isBlank(httpReponse)){
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode() , label + "返回参数为空");
        }

        ApiMainResponseVO responseVO = JSONObject.parseObject(httpReponse , ApiMainResponseVO.class);
        if(responseVO == null){
            throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode() , label + "返回参数为空");
        }

        if(StringUtils.isNotBlank(responseVO.getRespData())){
            String respData = AlgorithmUtils.desDecryptFromBase64(responseVO.getRespData() , msgKey);
            log.info("返回参数明文:{}", httpReponse);
            responseVO.setRespData(respData);
        }

        if(StringUtils.isNotBlank(responseVO.getSign())){
            String sign = AlgorithmUtils.getSHA256Hex(responseVO.getSignData(signKey)).toUpperCase();
            if(!StringUtils.equals(responseVO.getSign() , sign)){
                throw new BaseException(CommonErrorEnum.CHANNEL_ERROR.getCode() , "扫码富返回参数签名不一致");
            }
        }
        return responseVO;
    }
}