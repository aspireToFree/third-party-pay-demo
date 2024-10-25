package com.kz.tppd.test;

import com.kz.tppd.Application;
import com.kz.tppd.common.enums.ChannelCodeEnum;
import com.kz.tppd.common.enums.PayMethodEnum;
import com.kz.tppd.trade.dto.request.PayOrderQueryRequestDTO;
import com.kz.tppd.trade.dto.request.RefundQueryRequestDTO;
import com.kz.tppd.trade.dto.request.RefundRequestDTO;
import com.kz.tppd.trade.dto.request.UnifiedOrderRequestDTO;
import com.kz.tppd.trade.dto.response.UnifiedOrderResponseDTO;
import com.kz.tppd.trade.process.PayOrderQueryProcessService;
import com.kz.tppd.trade.process.RefundProcessService;
import com.kz.tppd.trade.process.RefundQueryProcessService;
import com.kz.tppd.trade.process.UnifiedOrderProcessService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 支付测试类
 * @author kz
 * @date 2024/10/7 14:41
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class PayTest {

    @Resource
    private UnifiedOrderProcessService unifiedOrderProcessService;

    @Resource
    private PayOrderQueryProcessService payOrderQueryProcessService;

    @Resource
    private RefundProcessService refundProcessService;

    @Resource
    private RefundQueryProcessService refundQueryProcessService;

    //微信公众号下单
    @Test
    public void wechatPublicOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();
        //微信公众号
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        //appId
        requestDTO.setAppId("xxxx");
        //openId
        requestDTO.setOpenId("xxxx");

        //TODO channelCode传 SMF，就是走SMF的通道，不传channelCode就是走微信通道
        requestDTO.setChannelCode(ChannelCodeEnum.SMF.getCode());

        //统一下单
        unifiedOrder(requestDTO);
    }

    //微信小程序下单
    @Test
    public void wechatMiniOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();
        //微信公众号
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_MINI);
        //appId
        requestDTO.setAppId("xxxx");
        //openId
        requestDTO.setOpenId("xxxx");

        //统一下单
        unifiedOrder(requestDTO);
    }

    //支付宝生活号下单
    @Test
    public void alipayPublicOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();

        //支付宝小程序
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
        //buyer_id字段（买家支付宝用户ID），支付宝未来计划逐步回收
        //requestDTO.setBuyerId("2088722007624736");

        //买家支付宝用户唯一标识（需要保证openId是对应appId下面的）
        requestDTO.setOpenId("073pJy6vonPdAv674u5To3b9pOB2u6ytj7iLrj851KuE9Qd");

        //统一下单
        unifiedOrder(requestDTO);
    }

    //支付宝小程序下单
    @Test
    public void alipayMiniOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();

        //支付宝小程序
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_MINI);
        //buyer_id字段（买家支付宝用户ID），支付宝未来计划逐步回收
        //requestDTO.setBuyerId("2088722007624736");

        //买家支付宝用户唯一标识（需要保证openId是对应appId下面的）
        requestDTO.setOpenId("073pJy6vonPdAv674u5To3b9pOB2u6ytj7iLrj851KuE9Qd");

        //统一下单
        unifiedOrder(requestDTO);
    }

    //支付宝APP下单
    @Test
    public void alipayAppOrder(){
        //TODO demo提供的支付宝沙箱参数是小程序的，APP测试需要用支付宝APP的appId参数来测试
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_APP);
        //buyer_id字段（买家支付宝用户ID），支付宝未来计划逐步回收
        //requestDTO.setBuyerId("2088722007624736");

        //买家支付宝用户唯一标识（需要保证openId是对应appId下面的）
        requestDTO.setOpenId("073pJy6vonPdAv674u5To3b9pOB2u6ytj7iLrj851KuE9Qd");

        //统一下单
        unifiedOrder(requestDTO);
    }

    //云闪付公众号下单
    @Test
    public void ysfPublicOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();

        //TODO demo里面，云闪付只支持接入扫码富的通道
        requestDTO.setPayMethodEnum(PayMethodEnum.YSF_PUBLIC);
        requestDTO.setOpenId("xxxx");

        //统一下单
        unifiedOrder(requestDTO);
    }

    /**
     * 统一下单
     * @param requestDTO 请求参数DTO
     * Created by kz on 2024/10/7 15:24.
     */
    private void unifiedOrder(UnifiedOrderRequestDTO requestDTO){
        requestDTO.setPayAmount(new BigDecimal("0.01"));
        requestDTO.setTradeDesc("1瓶饮料");
        //用户的IP
        requestDTO.setClientIp("127.0.0.1");

        UnifiedOrderResponseDTO responseDTO = unifiedOrderProcessService.executeProcess(requestDTO);
        if(responseDTO.isSuccess()){
            if(PayMethodEnum.ALIPAY_PUBLIC.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为支付宝交易号trade_no，用于前端支付宝JSAPI支付用，前端JS调起支付参照[https://opendocs.alipay.com/mini/api/openapi-pay]");
            } else if(PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为支付宝交易号trade_no，用于前端支付宝JSAPI支付用，前端JS调起支付参照[https://opendocs.alipay.com/mini/api/openapi-pay]");
            } else if(PayMethodEnum.ALIPAY_APP.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为支付宝APP调起支付需要信息的JSON字符串，前端APP调起支付参照[https://opendocs.alipay.com/open/01dcc0?pathHash=cf89b2be]");
            } else if(PayMethodEnum.WECHAT_PUBLIC.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信JSAPI调起支付需要信息的JSON字符串，前端JS调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012062925]");
            } else if(PayMethodEnum.WECHAT_MINI.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信JSAPI调起支付需要信息的JSON字符串，前端小程序调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012265791]");
            } else if(PayMethodEnum.WECHAT_APP.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信APP调起支付需要信息的JSON字符串，前端APP调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012265144]");
            } else if(PayMethodEnum.YSF_PUBLIC.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.payUrl 为云闪付公众号的地址，前端重定向跳转到该地址就可调起支付");
            }
        }
    }

    //支付订单下单
    @Test
    public void payOrderQuery(){
        //TODO demo需要根据 payMethod判断写死通道，实际业务可根据订单找到原来的通道，不用传 payMethod
        PayOrderQueryRequestDTO requestDTO = new PayOrderQueryRequestDTO();
        //微信公众号支付
//        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
//        requestDTO.setOrderNo("e06e53fa3ce74aaca996c8e2c01d7383");


        //TODO channelCode传 SMF，就是走SMF的通道，不传channelCode就是走微信通道
        requestDTO.setChannelCode(ChannelCodeEnum.SMF.getCode());
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        requestDTO.setOrderNo("21859469d6d34c9a87d8f055390d8a91");
        requestDTO.setChannelOrderNo("SMFT1848984953191272448");
        payOrderQueryProcessService.executeProcess(requestDTO);
    }

    //退款
    @Test
    public void refund(){
        //TODO demo需要根据 payMethod判断写死通道，实际业务可根据订单找到原来的通道，不用传 payMethod
        RefundRequestDTO requestDTO = new RefundRequestDTO();
        requestDTO.setRefundAmount(new BigDecimal("0.01"));
        requestDTO.setRefundReason("质量问题");

        //支付宝生活号退款
//        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
//        requestDTO.setOriginalOrderNo("d8e36cf3fbb5407c8fdb314de4ad37ec");

        //微信直联 微信公众号支付
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        requestDTO.setOriginalChannelOrderNo("4200002347202410174434240925");
        requestDTO.setOriginalOrderNo("YRJ1846866734863224832");
        requestDTO.setOriginalPayAmount(new BigDecimal("0.01"));    //原订单支付金额

        //扫码富间联 微信公众号退款
        //TODO channelCode传 SMF，就是走SMF的通道，不传channelCode就是走微信通道
//        requestDTO.setChannelCode(ChannelCodeEnum.SMF.getCode());
//        requestDTO.setOriginalChannelOrderNo("SMFT1848984953191272448");
//        requestDTO.setOriginalOrderNo("21859469d6d34c9a87d8f055390d8a91");
        refundProcessService.executeProcess(requestDTO);
    }

    //退款订单查询
    @Test
    public void refundQuery(){
        //TODO demo需要根据 payMethod判断写死通道，实际业务可根据订单找到原来的通道，不用传 payMethod
        RefundQueryRequestDTO requestDTO = new RefundQueryRequestDTO();

        //支付宝生活号
//        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
//        requestDTO.setOrderNo("d8e36cf3fbb5407c8fdb314de4ad37ec");

        //微信直联 微信公众号支付
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        requestDTO.setOrderNo("542d628ac7874672a342e177241e071a");

        //扫码富间联 微信公众号退款查询
        //TODO channelCode传 SMF，就是走SMF的通道，不传channelCode就是走微信通道
//        requestDTO.setChannelCode(ChannelCodeEnum.SMF.getCode());
//        requestDTO.setChannelOrderNo("SMFT1848986179974860800");
//        requestDTO.setOrderNo("a73c1b662e3945bfb609f60aa6e22801");
        refundQueryProcessService.executeProcess(requestDTO);
    }
}