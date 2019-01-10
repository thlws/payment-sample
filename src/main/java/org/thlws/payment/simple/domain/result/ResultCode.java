package org.thlws.payment.simple.domain.result;

/**
 * 响应结果编码
 * Created by  HanleyTang on 2018-12-29
 */
public enum ResultCode {

    ok("ok"),ng("ng");

    public final String code;

    ResultCode(String code) {
        this.code = code;
    }
}
