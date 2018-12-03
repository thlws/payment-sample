package org.thlws.payment.simple.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thlws.payment.simple.config.WechatConfig;

/**
 * @author HanleyTang
 * @date 2018-12-02
 */

@Controller
@RequestMapping("/wechat")
public class WechatPayController {

    private static final Log log = LogFactory.get();

    @Autowired
    private WechatConfig wechatConfig;

    @RequestMapping("/index")
    public String index(){
        return "wechat";
    }


    @RequestMapping("/pay/micro_pay")
    @ResponseBody
    public String microPay(@RequestParam(name = "amt")String amt,
                                             @RequestParam(name = "barcode")String barcode){
        try {

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }


    @RequestMapping("/pay/qrcode")
    @ResponseBody
    public String qrcode(@RequestParam(name = "amt")String amt){
        try {

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }


    @RequestMapping("/pay/mp")
    @ResponseBody
    public String mp(@RequestParam(name = "amt")String amt){
        try {

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }


    @RequestMapping("/refund")
    @ResponseBody
    public String refund(@RequestParam(name = "amt")String amt,
                                       @RequestParam(name = "out_trade_no")String outTradeNo){
        return null;
    }


}
