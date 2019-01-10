package org.thlws.payment.simple.domain.result;

/**
 * 响应结果编码
 * Created by  HanleyTang on 2018-12-29
 */
public enum ResultSubCode {

    system_error("system_error","系统异常"),
    request_not_from_wechat("request_not_from_wechat","请求不是来自微信"),
    member_not_exist("member_not_exist","会员不存在");

    public final String subCode;
    public final String subDesc;

    ResultSubCode(String subCode, String subDesc) {
        this.subCode = subCode;
        this.subDesc = subDesc;
    }}
