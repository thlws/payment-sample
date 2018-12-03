package org.thlws.payment.simple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 翼支付参数,bestpay 名称不是我取的，是电信官方的名称，不信你看它官方网站(https://www.bestpay.com.cn/),哈哈~
 * @author HanleyTang
 * @date 2018-12-03
 */
@Component
@ConfigurationProperties(prefix = "payment.bestpay")
@PropertySource(value = "payment.properties")
public class BestpayConfig {


    private String key;
    private String merchant_pwd;
    private String merchant_id;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMerchant_pwd() {
        return merchant_pwd;
    }

    public void setMerchant_pwd(String merchant_pwd) {
        this.merchant_pwd = merchant_pwd;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    @Override
    public String toString() {
        return "BestpayConfig{" +
                "key='" + key + '\'' +
                ", merchant_pwd='" + merchant_pwd + '\'' +
                ", merchant_id='" + merchant_id + '\'' +
                '}';
    }
}
