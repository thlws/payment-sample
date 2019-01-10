package org.thlws.payment.simple.domain.result;

import java.io.Serializable;

/**
 * Created by  HanleyTang on 2018-12-29
 */
public class Result implements Serializable {

    private String code;
    private String desc;
    private Object data;
    private String type = DataType.json.value;
    private String subCode;

    private Result(String code, String desc, Object data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    private Result(String code, String desc, Object data,String type) {
        this.code = code;
        this.desc = desc;
        this.data = data;
        this.type = type;
    }

    public static Result ok(Object data){
        return new Result(ResultCode.ok.code,null,data);
    }

    public static Result ok(Object data,String type){
        return new Result(ResultCode.ok.code,null,data,type);
    }

    public static Result ng(String subCode,String desc){
        Result result = new Result(ResultCode.ng.code,desc,null);
        result.setSubCode(subCode);
        return result;
    }

    public static Result ng(String subCode,String desc,String type){
        Result result = new Result(ResultCode.ng.code,desc,null,type);
        result.setSubCode(subCode);
        return result;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }
}
