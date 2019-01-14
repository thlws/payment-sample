package org.thlws.payment.simple.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thlws.payment.BestPayClient;
import org.thlws.payment.bestpay.entity.request.BarcodePayRequest;
import org.thlws.payment.bestpay.entity.request.OrderRefundRequest;
import org.thlws.payment.bestpay.entity.request.OrderReverseRequest;
import org.thlws.payment.bestpay.entity.request.QueryOrderRequest;
import org.thlws.payment.bestpay.entity.response.OrderRefundResponse;
import org.thlws.payment.bestpay.entity.response.OrderResultResponse;
import org.thlws.payment.bestpay.entity.response.OrderReverseResponse;
import org.thlws.payment.simple.config.BestpayConfig;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 翼支付
 *
 * @author HanleyTang
 * @date 2018 -12-02
 */
@Controller
@RequestMapping("/bestpay")
public class BestpayController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BestpayConfig config;

    /**
     * Index string.
     *
     * @return the string
     */
    @RequestMapping("/index")
    public String index(){
        return "bestpay";
    }


    /**
     * 适用线下POS付款，类似沃尔玛 店员扫码用户手机完成付款(用户需要安装翼支付App)
     * 线上环境一般不会这么使用。
     * @param amt     the amt
     * @param barcode the barcode
     * @return the object
     */
    @RequestMapping(value = "/pay",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object pay(@RequestParam(name = "amt")String amt,
                      @RequestParam(name = "barcode")String barcode){

        try {
            //单位为分
            String amount = new BigDecimal(amt).movePointRight(2).longValue()+"";

            BarcodePayRequest request = new BarcodePayRequest();
            request.setMerchantId(config.getMerchant_id());
            request.setBarcode(barcode);
            request.setOrderNo(IdUtil.fastSimpleUUID());
            request.setOrderReqNo(IdUtil.fastSimpleUUID());//交易流水号
            request.setOrderDate(DateUtil.format(new Date(),DatePattern.PURE_DATETIME_PATTERN));
            request.setOrderAmt(amount);
            request.setProductAmt(amount);
            request.setGoodsName("测试商品");
            request.setStoreId("20191008");
            OrderResultResponse response = BestPayClient.barcode(request, config.getKey());
            return response;
        } catch (Exception e) {
            logger.error("翼支付付款失败",e);
            return e;
        }

    }


    /**
     * 查询翼支付付款详情
     *
     * @param orderNo the order no
     * @return the object
     */
    @RequestMapping(value = "/query",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object query(@RequestParam(name = "orderNo")String orderNo,
                        @RequestParam(name = "reqNo")String reqNo){


        try {
            QueryOrderRequest request = new QueryOrderRequest();
            request.setMerchantId(config.getMerchant_id());
            request.setOrderNo(orderNo);//FIXME 支付单号：QueryOrderRequest.orderNo
            request.setOrderReqNo(reqNo);//FIXME  支付请求号 QueryOrderRequest.orderReqNo
            request.setOrderDate(DateUtil.format(new Date(),DatePattern.PURE_DATETIME_PATTERN));

            OrderResultResponse response = BestPayClient.query(request, config.getKey());
            return response;
        } catch (Exception e) {
            logger.error("翼支付查询失败",e);
            return e;
        }
    }


    /**
     * 撤销支付订单.
     *
     * @param orderNo the order no
     * @return the object
     */
    @RequestMapping(value = "/reverse",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object reverse(@RequestParam(name = "orderNo")String orderNo,
                          @RequestParam(name = "reqNo")String reqNo,
                          @RequestParam(name = "amt",defaultValue = "0.01")String amt){

        try {

            //单位为分
            String amount = new BigDecimal(amt).movePointRight(2).longValue()+"";

            OrderReverseRequest request = new OrderReverseRequest();
            request.setMerchantId(config.getMerchant_id());
            request.setMerchantPwd(config.getMerchant_pwd());
            request.setOldOrderNo(orderNo);//FIXME 支付单号：QueryOrderRequest.orderNo
            request.setOldOrderReqNo(reqNo);//FIXME  支付请求号 QueryOrderRequest.orderReqNo
            request.setRefundReqNo(IdUtil.fastSimpleUUID());
            request.setRefundReqDate(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN));
            request.setTransAmt(amount);

            OrderReverseResponse response = BestPayClient.reverse(request, config.getKey());
            return response;
        } catch (Exception e) {
            logger.error("翼支付撤销失败",e);
            return e;
        }
    }


    /**
     * 退款
     *
     * @param orderNo the order no
     * @return the object
     */
    @RequestMapping(value = "/refund",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object refund(@RequestParam(name = "orderNo")String orderNo,
                         @RequestParam(name = "reqNo")String reqNo,
                         @RequestParam(name = "amt",defaultValue = "0.01")String amt){

        try {


            //单位为分
            String amount = new BigDecimal(amt).movePointRight(2).longValue()+"";

            OrderRefundRequest request = new OrderRefundRequest();
            request.setMerchantId(config.getMerchant_id());
            request.setOldOrderNo(orderNo);//FIXME 支付单号：QueryOrderRequest.orderNo
            request.setOldOrderReqNo(reqNo);//FIXME  支付请求号 QueryOrderRequest.orderReqNo
            request.setMerchantPwd(config.getMerchant_pwd());
            request.setRefundReqDate(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN));
            request.setRefundReqNo(RandomUtil.randomNumbers(16));
            request.setTransAmt(amount);

            OrderRefundResponse response = BestPayClient.refund(request, config.getKey());
            return response;
        } catch (Exception e) {
            logger.error("翼支付退款失败",e);
            return e;
        }
    }


}
