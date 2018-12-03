package org.thlws.payment.simple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 微信配置
 * @author HanleyTang
 * @date 2018-12-03
 */
@Component
@ConfigurationProperties(prefix = "payment.wechat")
@PropertySource(value = "payment.properties")
public class WechatConfig {


    private String appid;
    private String app_secret;
    private String api_key;
    private String mch_id;
    private String sub_mch_id;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    @Override
    public String toString() {
        return "WechatConfig{" +
                "appid='" + appid + '\'' +
                ", app_secret='" + app_secret + '\'' +
                ", api_key='" + api_key + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", sub_mch_id='" + sub_mch_id + '\'' +
                '}';
    }
}
