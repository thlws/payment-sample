package org.thlws.payment.simple.domain.result;

public enum DataType {

    url("url"),
    json("json"),
    xml("xml");

    public final String value;

    DataType(String value) {
        this.value = value;
    }
}
