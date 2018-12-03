package org.thlws.payment.simple.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alipay.api.AlipayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thlws.payment.AlipayClient;
import org.thlws.payment.alipay.core.AlipayCore;
import org.thlws.payment.alipay.entity.request.AlipayMobileSiteRequest;
import org.thlws.payment.alipay.entity.request.AlipayRefundRequest;
import org.thlws.payment.alipay.entity.request.AlipayTradeRequest;
import org.thlws.payment.alipay.entity.request.AlipayWebSiteRequest;
import org.thlws.payment.alipay.entity.response.AlipayRefundResponse;
import org.thlws.payment.alipay.entity.response.AlipayTradeResponse;
import org.thlws.payment.simple.config.AlipayConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author HanleyTang
 * @date 2018-12-02
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {

    private static final Log log = LogFactory.get();

    @Autowired
    private AlipayConfig alipayConfig;

    @RequestMapping("/index")
    public String index(){
        return "alipay";
    }

    @RequestMapping("/pay/f2f")
    @ResponseBody
    public AlipayTradeResponse payFaceToFace(@RequestParam(name = "amt")String amt,
                                @RequestParam(name = "barcode")String barcode){
        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayTradeRequest request = new AlipayTradeRequest();
            request.setTotalAmount(amt);
            request.setStoreId("myStoreId001");
            request.setOperatorId("007");
            request.setAuthCode(barcode);
            request.setOutTradeNo(System.currentTimeMillis()+"");
            request.setSubject("测试当面付");

            AlipayTradeResponse response = AlipayClient.pay(request,alipayCore);
            return response;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    @RequestMapping("/pay/mobile")
    public String payInMobileSite(@RequestParam(name = "amt")String amt,
                                  ModelMap modelMap){

        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayMobileSiteRequest request = new AlipayMobileSiteRequest();
            request.setNotifyUrl("http://116.228.212.37:8080/alipay/notify_mobile");
            request.setReturnUrl("http://localhost:8080/alipay/alipay_sync_mobile");
            AlipayMobileSiteRequest.BizContent bizContent = new AlipayMobileSiteRequest.BizContent();
            bizContent.setTotalAmount("0.01");
            bizContent.setSubject("测试H5(手机网页)支付");
            //bizContent.setSeller_id(partner_id);
            bizContent.setProductCode("p0001");
            bizContent.setOutTradeNo(System.currentTimeMillis()+"");
            request.setBizContent(bizContent);
            String html = AlipayClient.payInMobileSite(request,alipayCore);

            modelMap.addAttribute("form", html);



        } catch (Exception e) {
            log.error(e);
        }

        return "alipay/alipay_mobile";
    }

    @RequestMapping("/pay/web")
    public String payInWebSite(@RequestParam(name = "amt")String amt,
                               ModelMap modelMap){


        try {

            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();


            AlipayWebSiteRequest request = new AlipayWebSiteRequest();
            request.setNotifyUrl("http://116.228.212.37:8080/alipay/notify_mobile");
            request.setReturnUrl("http://localhost:8080/alipay/alipay_sync_web");
            AlipayWebSiteRequest.BizContent bizContent = new AlipayWebSiteRequest.BizContent();
            bizContent.setTotalAmount("0.01");
            bizContent.setSubject("测试电脑网站支付");
            bizContent.setBody("测试");
            bizContent.setProductCode("p0001");
            bizContent.setOutTradeNo(System.currentTimeMillis()+"");
            String html = AlipayClient.payInWebSite(request,alipayCore);
            modelMap.addAttribute("form", html);

        } catch (Exception e) {
            log.error(e);
        }


        return "alipay/alipay_web";
    }

    @RequestMapping("/refund")
    @ResponseBody
    public AlipayRefundResponse refund(@RequestParam(name = "amt")String amt,
                         @RequestParam(name = "out_trade_no")String outTradeNo){

        try {

            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayRefundRequest request = new AlipayRefundRequest();
            request.setTradeNo(outTradeNo);
            request.setRefundAmount(amt);
            request.setRefundReason("测试退款");
            AlipayRefundResponse response = AlipayClient.refund(request,alipayCore);
            return response;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }


    @RequestMapping("/alipay_sync_mobile")
    public String sync_mobile(HttpServletRequest request){

        System.out.println("================sync_mobile===================");
        Enumeration<String> its = request.getParameterNames();
        while (its.hasMoreElements()){
            String name = its.nextElement();
            System.out.println("name = [" + name + "]");
        }
        System.out.println("================sync_mobile===================");
        return "alipay/alipay_sync_mobile";
    }


    @RequestMapping("/alipay_sync_web")
    public String sync_web(HttpServletRequest request){

        System.out.println("==============sync_web=====================");
        Enumeration<String> its = request.getParameterNames();
        while (its.hasMoreElements()){
            String name = its.nextElement();
            System.out.println("name = [" + name + "]");
        }
        System.out.println("===============sync_web====================");
        return "alipay/alipay_sync_web";
    }

}
