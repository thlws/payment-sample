package org.thlws.payment.simple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置
 * @author HanleyTang
 * @date 2018-12-03
 */
@Component
@ConfigurationProperties(prefix = "payment.alipay")
@PropertySource(value = "payment.properties")
public class AlipayConfig {

    private String appid;
    private String private_key;
    private String alipay_public_key;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }


    @Override
    public String toString() {
        return "AlipayConfig{" +
                "appid='" + appid + '\'' +
                ", private_key='" + private_key + '\'' +
                ", alipay_public_key='" + alipay_public_key + '\'' +
                '}';
    }
}
