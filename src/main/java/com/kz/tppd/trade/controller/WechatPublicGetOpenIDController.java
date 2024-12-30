package com.kz.tppd.trade.controller;

import com.kz.tppd.trade.service.WechatPublicGetOpenIDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信公众号获取 OpenID Controller
 * @author kz
 * @date 2024/12/25 11:08
 */
@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/wechat/")
public class WechatPublicGetOpenIDController {

    @Resource
    private WechatPublicGetOpenIDService wechatPublicGetOpenIDService;

    //获取code
    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
    public void getCode(HttpServletRequest request , HttpServletResponse response) throws IOException {
        //获取code重定向地址
        String pageUrl = wechatPublicGetOpenIDService.getCode(request);
        response.sendRedirect(pageUrl);
    }


    /**
     获取openId
     @param code 微信code
     @param state 获取微信code时，传的附加参数
     **/
    @RequestMapping(value = "/getOpenId",method = RequestMethod.GET)
    public void getOpenId(@RequestParam("code") String code, @RequestParam("state") String state , HttpServletResponse response) throws IOException {
        String pageUrl = wechatPublicGetOpenIDService.wechatCode(code , state);
        //重定向回前端
        response.sendRedirect(pageUrl);
    }
}