package org.thlws.payment.simple.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alipay.api.AlipayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import org.thlws.payment.alipay.entity.response.AlipayQueryResponse;
import org.thlws.payment.alipay.entity.response.AlipayRefundResponse;
import org.thlws.payment.alipay.entity.response.AlipayTradeResponse;
import org.thlws.payment.simple.config.AlipayConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 支付宝支付示例.
 * FIXME 示例中 [手机+电脑] 网站支付暂不可用,我这里没有进行相关配置而已。
 * @author HanleyTang
 * @date 2018 -12-02
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {

    private static final Log log = LogFactory.get();

    @Autowired
    private AlipayConfig alipayConfig;

    /**
     * 支付宝付款测试页.
     *
     * @return the string
     */
    @RequestMapping("/index")
    public String index(){
        return "alipay";
    }

    /**
     * 当面付.
     * 用于线下POS，线上一般不会这么用
     *
     * @param amt     the amt
     * @param barcode the barcode
     * @return the object
     */
    @RequestMapping(value = "/pay/f2f",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object payFaceToFace(@RequestParam(name = "amt")String amt,
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
            log.error("当面付失败",e);
            return e;
        }

    }

    /**
     * 手机网站支付.
     * 在手机浏览器打开(不能在微信中)完成付款动作.
     *
     * @param amt      the amt
     * @param modelMap the model map
     * @return the string
     */
    @RequestMapping("/pay/mobile")
    public String payInMobileSite(@RequestParam(name = "amt",defaultValue = "0.01")String amt,
                                  ModelMap modelMap){

        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayMobileSiteRequest request = new AlipayMobileSiteRequest();
            request.setNotifyUrl(null);

            //http://你的同步处理地址/alipay/alipay_sync_mobile
            request.setReturnUrl("http://localhost:7171/alipay/alipay_sync_mobile");
            AlipayMobileSiteRequest.BizContent bizContent = new AlipayMobileSiteRequest.BizContent();
            bizContent.setTotalAmount(amt);
            bizContent.setSubject("测试H5(手机网页)支付");
//            bizContent.setSeller_id(partner_id);
            bizContent.setProductCode("QUICK_WAP_WAY");
            bizContent.setOutTradeNo(System.currentTimeMillis()+"");
            request.setBizContent(bizContent);
            String html = AlipayClient.payInMobileSite(request,alipayCore);
            modelMap.addAttribute("form", html);
        } catch (Exception e) {
            log.error("手机网站支付失败",e);
        }
        return "alipay/alipay_mobile";
    }

    //http://你的异步处理地址/alipay/notify_mobile
    /**
     * 电脑网站支付.
     *
     * @param amt      the amt
     * @param modelMap the model map
     * @return the string
     */
    @RequestMapping("/pay/web")
    public String payInWebSite(@RequestParam(name = "amt",defaultValue = "0.01")String amt,
                               ModelMap modelMap){
        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayWebSiteRequest request = new AlipayWebSiteRequest();

            //http://你的异步处理地址/alipay/notify_mobile
            //request.setNotifyUrl("http://你的异步处理地址/alipay/notify_mobile");
            request.setNotifyUrl(null);

            request.setReturnUrl("http://localhost:7171/alipay/alipay_sync_web");

            AlipayWebSiteRequest.BizContent bizContent = new AlipayWebSiteRequest.BizContent();
            bizContent.setTotalAmount(amt);
            bizContent.setSubject("测试电脑网站支付");
            bizContent.setBody("测试");
            bizContent.setProductCode("FAST_INSTANT_TRADE_PAY");
            bizContent.setOutTradeNo(System.currentTimeMillis()+"");
            request.setBizContent(bizContent);
            String html = AlipayClient.payInWebSite(request,alipayCore);
            modelMap.addAttribute("form", html);

        } catch (Exception e) {
            log.error("电脑网站支付失败",e);
        }

        return "alipay/alipay_web";
    }

    /**
     * 查询,适用所有付款方式.
     *
     * @param outTradeNo the out trade no
     * @return the object
     */
    @RequestMapping(value = "/query",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object query(@RequestParam(name = "out_trade_no")String outTradeNo){

        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();


            AlipayQueryResponse response = AlipayClient.query(outTradeNo, alipayCore);
            return response;
        } catch (Exception e) {
            log.error("支付宝查询失败.",e);
            return e;
        }
    }


    @RequestMapping(value = "/refund",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object refund(@RequestParam(name = "amt")String amt,
                         @RequestParam(name = "out_trade_no")String outTradeNo){

        try {
            AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
            AlipayCore alipayCore = clientBuilder.setAlipayPublicKey(alipayConfig.getAlipay_public_key())
                    .setAppId(alipayConfig.getAppid())
                    .setPrivateKey(alipayConfig.getPrivate_key())
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();

            AlipayRefundRequest request = new AlipayRefundRequest();
            request.setOutTradeNo(outTradeNo);
            request.setRefundAmount(amt);
            request.setRefundReason("测试退款");
            request.setOutRequestNo(System.currentTimeMillis() + "");
            AlipayRefundResponse response = AlipayClient.refund(request, alipayCore);
            return response;
        } catch (Exception e) {
            log.error("支付宝退款失败.",e);
            return e;
        }
    }


    /**
     * 手机网站支付同步跳转页面
     *
     * @param request the request
     * @return the string
     */
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


    /**
     * 电脑网站支付同步跳转页面.
     */
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
