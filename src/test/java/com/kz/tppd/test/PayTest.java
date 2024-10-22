package com.kz.tppd.test;

import com.kz.tppd.Application;
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

    //小程序下单
    @Test
    public void miniOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();

        //支付宝小程序
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_MINI);
        //buyer_id字段（买家支付宝用户ID），支付宝未来计划逐步回收
        //requestDTO.setBuyerId("2088722007624736");

        //买家支付宝用户唯一标识（需要保证openId是对应appId下面的）
        requestDTO.setOpenId("073pJy6vonPdAv674u5To3b9pOB2u6ytj7iLrj851KuE9Qd");


        //统一下单
        UnifiedOrderResponseDTO responseDTO = unifiedOrder(requestDTO);
        if(responseDTO.isSuccess()){
            if(PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为支付宝交易号trade_no，用于前端支付宝JSAPI支付用");
            }
        }
    }

    //公众号下单
    @Test
    public void publicOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();

        //支付宝生活号
//        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
//        //buyer_id字段（买家支付宝用户ID），支付宝未来计划逐步回收
//        requestDTO.setBuyerId("2088722007624736");

        //buyer_open_id（买家支付宝用户唯一标识）（需要保证openId是对应appId下面的）
        //requestDTO.setOpenId("073pJy6vonPdAv674u5To3b9pOB2u6ytj7iLrj851KuE9Qd");



        //微信公众号
        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        //appId
        requestDTO.setAppId("wx8d44cff06a153244");
        //openId
        requestDTO.setOpenId("oEb7E6rh2NEjqCzJMu-ZtXf8l0k0");


        //统一下单
        UnifiedOrderResponseDTO responseDTO = unifiedOrder(requestDTO);
        if(responseDTO.isSuccess()){
            if(PayMethodEnum.ALIPAY_MINI.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为支付宝交易号trade_no，用于前端支付宝JSAPI支付用");
            } else if(PayMethodEnum.WECHAT_PUBLIC.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信JSAPI调起支付需要信息的JSON字符串，前端JS调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012062925]");
            } else if(PayMethodEnum.WECHAT_MINI.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信JSAPI调起支付需要信息的JSON字符串，前端小程序调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012265791]");
            } else if(PayMethodEnum.WECHAT_APP.equals(requestDTO.getPayMethodEnum())){
                log.info("responseDTO.attach 为微信APP调起支付需要信息的JSON字符串，前端APP调起支付参照[https://pay.weixin.qq.com/doc/v3/merchant/4012265144]");
            }
        }
    }

    //APP下单
    @Test
    public void appOrder(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_APP);

        //统一下单
        unifiedOrder(requestDTO);
    }

    /**
     * 统一下单
     * @param requestDTO 请求参数DTO
     * Created by kz on 2024/10/7 15:24.
     */
    private UnifiedOrderResponseDTO unifiedOrder(UnifiedOrderRequestDTO requestDTO){
        requestDTO.setPayAmount(new BigDecimal("0.01"));
        requestDTO.setTradeDesc("1瓶饮料");
        return unifiedOrderProcessService.executeProcess(requestDTO);
    }

    //支付订单下单
    @Test
    public void payOrderQuery(){
        PayOrderQueryRequestDTO requestDTO = new PayOrderQueryRequestDTO();
//        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
//        requestDTO.setOrderNo("d8e36cf3fbb5407c8fdb314de4ad37ec");

        requestDTO.setPayMethodEnum(PayMethodEnum.WECHAT_PUBLIC);
        requestDTO.setOrderNo("e06e53fa3ce74aaca996c8e2c01d7383");
        payOrderQueryProcessService.executeProcess(requestDTO);
    }

    //退款
    @Test
    public void refund(){
        RefundRequestDTO requestDTO = new RefundRequestDTO();

        //支付宝生活号退款
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
        requestDTO.setOriginalOrderNo("d8e36cf3fbb5407c8fdb314de4ad37ec");

        requestDTO.setRefundAmount(new BigDecimal("10"));
        requestDTO.setRefundReason("质量问题");
        refundProcessService.executeProcess(requestDTO);
    }

    //退款订单查询
    @Test
    public void refundQuery(){
        RefundQueryRequestDTO requestDTO = new RefundQueryRequestDTO();

        //支付宝生活号
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);
        requestDTO.setOrderNo("cdb5cefcdfbc4e128ba8e061212f0266");
        requestDTO.setOriginalOrderNo("d8e36cf3fbb5407c8fdb314de4ad37ec");

        refundQueryProcessService.executeProcess(requestDTO);
    }
}