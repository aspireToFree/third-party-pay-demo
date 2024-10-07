package com.kz.tppd.test;

import com.kz.tppd.Application;
import com.kz.tppd.common.enums.PayMethodEnum;
import com.kz.tppd.trade.dto.request.UnifiedOrderRequestDTO;
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

    @Test
    public void aaa(){
        UnifiedOrderRequestDTO requestDTO = new UnifiedOrderRequestDTO();
        requestDTO.setPayAmount(new BigDecimal("10"));
        requestDTO.setTradeDesc("1瓶饮料");
        requestDTO.setPayMethodEnum(PayMethodEnum.ALIPAY_PUBLIC);

        unifiedOrderProcessService.executeProcess(requestDTO);
    }
}