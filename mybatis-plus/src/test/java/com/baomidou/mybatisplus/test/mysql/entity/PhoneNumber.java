package com.baomidou.mybatisplus.test.mysql.entity;

/**
 * @author junyu
 * @Date 2016-09-09
 */

public class PhoneNumber {
    private String countryCode;
    private String stateCode;
    private String number;

    public PhoneNumber() {
    }

    public PhoneNumber(String countryCode, String stateCode, String
            number) {
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.number = number;
    }

    public PhoneNumber(String string) {
        if (string != null) {
            String[] parts = string.split("-");
            if (parts.length > 0) this.countryCode = parts[0];
            if (parts.length > 1) this.stateCode = parts[1];
            if (parts.length > 2) this.number = parts[2];
        }
    }

    public String getAsString() {
        return countryCode + "-" + stateCode + "-" + number;

    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return getAsString();
    }
}
