package org.thlws.payment.simple.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HanleyTang
 * @date 2018-12-02
 */
@Controller("/alipay")
public class AlipayController {

    @RequestMapping("/pay/f2f")
    public String payFaceToFace(){
        return null;
    }

    @RequestMapping("/pay/mobile")
    public String payInMobileSite(){
        return null;
    }

    @RequestMapping("/pay/web")
    public String payInWebSite(){
        return null;
    }

    @RequestMapping("/refund")
    public String refund(){
        return null;
    }

    @RequestMapping("/cancel")
    public String cancel(){
        return null;
    }

    @RequestMapping("/query")
    public String query(){
        return null;
    }



}
