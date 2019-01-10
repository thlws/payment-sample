package org.thlws.payment.simple.util;

/**
 * Created by  HanleyTang on 2019-01-10
 */
public class WechatUrlUtil {

    public  static String scriptUrl(String url){
        StringBuffer mUrl = new StringBuffer("<script>window.location.href = '");
        mUrl.append(url).append("';").append("</script>");
        return mUrl.toString();
    }
}
