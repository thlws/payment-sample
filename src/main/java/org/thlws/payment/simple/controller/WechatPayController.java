package org.thlws.payment.simple.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thlws.payment.WechatMpClient;
import org.thlws.payment.WechatPayClient;
import org.thlws.payment.simple.config.WechatConfig;
import org.thlws.payment.simple.util.WechatUrlUtil;
import org.thlws.payment.wechat.entity.dto.MpPayment;
import org.thlws.payment.wechat.entity.request.UnifiedOrderRequest;
import org.thlws.payment.wechat.entity.request.WechatPayRequest;
import org.thlws.payment.wechat.entity.request.WechatRefundRequest;
import org.thlws.payment.wechat.entity.request.mp.MpObtainOauthTokenRequest;
import org.thlws.payment.wechat.entity.response.NotifyResponse;
import org.thlws.payment.wechat.entity.response.UnifiedOrderResponse;
import org.thlws.payment.wechat.entity.response.WechatPayResponse;
import org.thlws.payment.wechat.entity.response.WechatRefundResponse;
import org.thlws.payment.wechat.entity.response.mp.MpOauthTokenResponse;
import org.thlws.payment.wechat.type.WechatTradeType;
import org.thlws.payment.wechat.utils.WechatUtil;
import org.thlws.utils.ThlwsBeanUtil;
import org.thlws.utils.ZxingUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * The type Wechat pay controller.
 *
 * @author HanleyTang
 * @date 2018 -12-02
 */
@Controller
@RequestMapping("/wechat")
public class WechatPayController {

    private static final Log log = LogFactory.get();

    /**
     * 微信异步处理成功 返回数据.
     */
    public static final String XML_NOTIFY_OK = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[处理成功]]></return_msg></xml>";

    /**
     * 微信异步处理失败 返回数据.
     */
    public static final String XML_NOTIFY_NG = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";


    @Autowired
    private WechatConfig config;

    /**
     * 微信示例首页
     *
     * @return the string
     */
    @RequestMapping("/index")
    public String index(){
        return "wechat";
    }


    /**
     * 刷卡支付
     *
     * @param amt     the amt
     * @param barcode the barcode
     * @return the object
     */
    @RequestMapping(value = "/pay/micro_pay",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object microPay(@RequestParam(name = "amt")String amt,
                           @RequestParam(name = "barcode")String barcode){
        try {
            WechatPayRequest request = new WechatPayRequest();
            request.setAppId(config.getAppid());
            request.setMchId(config.getMch_id());

            BigDecimal amount = new BigDecimal(amt);//测试金额
            System.out.println("amount="+amount);
            BigDecimal wechatAmt = amount.movePointRight(2);//微信单位为分，实际金额x100

            request.setSpbillCreateIp(NetUtil.getLocalhostStr());
            request.setTotalFee(wechatAmt.longValue()+"");//微信单位为分
            request.setAttach("attach data");
            request.setOutTradeNo(ThlwsBeanUtil.getRandomString(32));
            request.setAuthCode(barcode);
            request.setDeviceInfo("myDevice");
            request.setBody("pay test");

            WechatPayResponse response = WechatPayClient.microPay(request,config.getApi_key());
            return response;
        } catch (Exception e) {
            log.error("刷卡支付 microPay 付款失败",e);
            return e;
        }

    }


    /**
     * 需要异步处理,流程无法完整演示
     *
     * 扫描支付,官方叫native
     *
     * @param amt the amt
     * @return the object
     */
    @RequestMapping(value = "/pay/qrcode",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] qrcode(@RequestParam(name = "amt")String amt){
        try {
            UnifiedOrderRequest request = new UnifiedOrderRequest();
            request.setAppId(config.getAppid());
            request.setMchId(config.getMch_id());

            BigDecimal amount = new BigDecimal(amt);//测试金额
            BigDecimal wechatAmt = amount.movePointRight(2);//微信单位为分，实际金额x100


            String body = "动态二维码支付测试";
            request.setBody(body);
            request.setOutTradeNo(System.currentTimeMillis()+"");
            request.setTotalFee(wechatAmt.longValue()+"");//微信单位为分
            request.setTradeType(WechatTradeType.NATIVE.type);
            request.setNotifyUrl("http://部署该服务的地址/wechat/notify");
            request.setSpbillCreateIp(NetUtil.getLocalhostStr());

            UnifiedOrderResponse response = WechatPayClient.unifiedOrder(request,config.getApi_key());
            String prepayId = response.getPrepayId();
            String imageContent = response.getCodeUrl();

            StringBuffer imgPath = new StringBuffer("/zone/temp/");
            File dir = new File(imgPath.toString());
            if (!dir.exists()){
                dir.mkdirs();
            }
            imgPath.append(prepayId).append(".png");

            ZxingUtil.qrCode(250,250,imageContent,"png",imgPath.toString());

            File img = new File(imgPath.toString());

            FileInputStream inputStream = new FileInputStream(img);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;

        } catch (Exception e) {
            log.error("扫码支付 qrcode 失败",e);
            return null;
        }

    }


    /****
     * 需要异步处理,公众号配置,流程无法演示
     *
     * 发起公众号支付,这里其实是公众号支付的数据准备阶段
     * 真正的支付在微信中调用微信JSSDK完成付款的.
     * @param amt the amt
     * @param modelMap the model map
     * @return object
     */
    @RequestMapping("/pay/mp")
    //@ResponseBody  //前后端分离模式需要直接返回JSON
    public Object mp(@RequestParam(name = "amt")String amt, ModelMap modelMap){
        try {

            UnifiedOrderRequest request = new UnifiedOrderRequest();
            request.setAppId(config.getAppid());
            request.setMchId(config.getMch_id());

            String outTradeNo = IdUtil.fastSimpleUUID();

            request.setOpenId("ofSpd1T-jkJ0UNzmHzDAd5Q2V_EM");
            // 1.若没有openId需要从微信中获取,使用如下方式生成微信规则URL用于得到openId
            // 2.WechatMpClient.generateWechatUrl(String appId, AuthorizeType scope, String callback, String bizData)
            // 3.得到openIdUrl后，返回到用户页面端，由用户发起请求,微信会自动回调我们填写的callback地址
            // 4.在回调地址callback中调用 WechatMpClient.obtainOauthAccessToken 就能得到openId，然后存储OpenId 下次付款就不用再获取了.
            // String callback = "微信回调地址"; 比如: http://wwww.xxx.com/wechat/openid
            // String bizData = "业务数据";//这里任意填写,最好使用有意义的值,在回调处理中可以得到微信参数.
            // String openIdUrl = WechatMpClient.generateWechatUrl(config.getAppid(), AuthorizeType.snsapi_base,callback,bizData);
            // return Result.ok("", DataType.url.value);


            BigDecimal amount = new BigDecimal(amt);//测试金额
            BigDecimal wechatAmt = amount.movePointRight(2);//微信单位为分，实际金额x100

            request.setBody("公众号支付测试");
            request.setOutTradeNo(outTradeNo);
            request.setTotalFee(wechatAmt.longValue()+"");//微信单位为分
            request.setTradeType(WechatTradeType.JSAPI.type);
            request.setNotifyUrl("http://部署该服务的地址/wechat/notify");
            request.setSpbillCreateIp(NetUtil.getLocalhostStr());

            UnifiedOrderResponse uResponse = WechatPayClient.unifiedOrder(request,config.getApi_key());

            MpPayment mPayment = new MpPayment();
            mPayment.setAppId(uResponse.getAppId());
            mPayment.setNonceStr(uResponse.getNonceStr());
            mPayment.setPackageStr("prepay_id=" + uResponse.getPrepayId());
            mPayment.setSignType("MD5");
            mPayment.setOutTradeNo(outTradeNo);
            mPayment.setTimeStamp(String.valueOf( System.currentTimeMillis() / 1000));
            mPayment.setPaySign(WechatUtil.sign(mPayment, config.getApi_key()));

            modelMap.addAttribute("mp", mPayment);

            //返回 支付数据, 如果是前后端分离的情况,需直接返回JSON格式支付数据,有前端开发人员处理.
            //return Result.ok(mPayment);

            //这里并非前后端分离方式,直接返回支付页面进行支付
            return "wechat_pay";

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }


    /**
     * 退款接口
     *
     * @param amt        the amt
     * @param outTradeNo the out trade no
     * @return the object
     */
    @RequestMapping(value = "/refund",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object refund(@RequestParam(name = "amt")String amt,
                         @RequestParam(name = "out_trade_no")String outTradeNo){

        try {
            String p12FilePath = "微信p12秘钥文件";//

            log.debug("微信[退款]测试开始-WechatPayClient.refund");

            BigDecimal amount = new BigDecimal(amt);//测试金额
            BigDecimal wechatAmt = amount.movePointRight(2);//微信单位为分，实际金额x100

            WechatRefundRequest request = new WechatRefundRequest();
            request.setAppId(config.getAppid());
            request.setMchId(config.getMch_id());

            request.setOutTradeNo(outTradeNo);
            request.setOutRefundNo(ThlwsBeanUtil.getRandomString(24));
            request.setTotalFee(wechatAmt.longValue()+"");//微信单位为分
            request.setRefundFee(wechatAmt.longValue()+"");

            WechatRefundResponse response = WechatPayClient.refund(request,config.getApi_key(),p12FilePath);
            return  response;
        } catch (Exception e) {
           log.error("微信退款失败",e);
           return e;
        }

    }


    /**
     * 得到OpenId.
     *
     * @param request the request
     * @return the object
     */
    @RequestMapping(value = "/openid", produces = MediaType.TEXT_HTML_VALUE)//直接返回一个URL自动跳转
    @ResponseBody
    public Object openId(HttpServletRequest request) {

        try {
            String code = request.getParameter("code");
            String bizData = request.getParameter("state");

            //FIXME 根据 bizData 得到 appId ,secret
            String appId="";
            String secret = "";

            MpObtainOauthTokenRequest oauthTokenRequest = new MpObtainOauthTokenRequest();
            oauthTokenRequest.setAppid(appId);
            oauthTokenRequest.setCode(code);
            oauthTokenRequest.setSecret(secret);
            MpOauthTokenResponse oauthTokenResponse = WechatMpClient.obtainOauthAccessToken(oauthTokenRequest);
            String openId = oauthTokenResponse.getOpenid();

            //TODO 存储 OpenId 到系统中
            //TODO 再返回前端页面,重新发起付款流程.
            return WechatUrlUtil.scriptUrl("前端页面URL");//前端会自动跳转到该页面.

        } catch (Exception e) {
            log.error("OpenId获取失败,error",e);
            return e;
        }

    }


    /**
     * 异步处理,流程无法演示
     *
     * 微信支付异步数据处理.
     *
     * @param request  the request
     * @param response the response
     */
    @RequestMapping("/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response){

        PrintWriter writer = null;

        try {
            writer = response.getWriter();
            String notifyMsgToXml = WechatUtil.parseNotifyMsgToXml(request);
            NotifyResponse nResponse = WechatUtil.parseNotifyMsgToBean(notifyMsgToXml);
            String outTradeNo = nResponse.getOut_trade_no();
            String transactionId = nResponse.getTransaction_id();
            //FIXME 这里需要根据 outTradeNo 标识该笔支付已经成功，并补填微信平台支付单号transactionId.
            writer.println(XML_NOTIFY_OK);
        } catch (Exception e) {
            log.error("异步处理失败",e);
            //注意如果返回NG,微信间隔一段时间会再次进行通知,记得必要时需要进行处理
            //比如连续3词都失败了，在系统记录数据，直接返回微信OK标识,然后人为介入排查原因.
            writer.println(XML_NOTIFY_NG);
        }

    }

}
