package com.libereco.springsocial.etsy.api;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentTemplateInfo {

    @JsonProperty("allow_check")
    private boolean allowCheck;

    @JsonProperty("allow_mo")
    private boolean allowMo;

    @JsonProperty
    private String state;

    @JsonProperty
    private String zip;

    @JsonProperty("allow_other")
    private boolean allowOther;

    @JsonProperty("allow_paypal")
    private boolean allowPaypal;

    @JsonProperty
    private String city;

    @JsonProperty("country_id")
    private int countryId;

    @JsonProperty("first_line")
    private String firstLine;

    @JsonProperty("name")
    private String name;

    @JsonProperty("paypal_email")
    private String paypalEmail;

    @JsonProperty("second_line")
    private String secondLine;

    @JsonIgnore
    public boolean isAllowCheck() {
        return allowCheck;
    }

    @JsonIgnore
    public void setAllowCheck(boolean allowCheck) {
        this.allowCheck = allowCheck;
    }

    @JsonIgnore
    public boolean isAllowMo() {
        return allowMo;
    }

    @JsonIgnore
    public void setAllowMo(boolean allowMo) {
        this.allowMo = allowMo;
    }

    @JsonIgnore
    public String getState() {
        return state;
    }

    @JsonIgnore
    public void setState(String state) {
        this.state = state;
    }

    @JsonIgnore
    public String getZip() {
        return zip;
    }

    @JsonIgnore
    public void setZip(String zip) {
        this.zip = zip;
    }

    @JsonIgnore
    public boolean isAllowOther() {
        return allowOther;
    }

    @JsonIgnore
    public void setAllowOther(boolean allowOther) {
        this.allowOther = allowOther;
    }

    @JsonIgnore
    public boolean isAllowPaypal() {
        return allowPaypal;
    }

    @JsonIgnore
    public void setAllowPaypal(boolean allowPaypal) {
        this.allowPaypal = allowPaypal;
    }

    @JsonIgnore
    public String getCity() {
        return city;
    }

    @JsonIgnore
    public void setCity(String city) {
        this.city = city;
    }

    @JsonIgnore
    public int getCountryId() {
        return countryId;
    }

    @JsonIgnore
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    @JsonIgnore
    public String getFirstLine() {
        return firstLine;
    }

    @JsonIgnore
    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getPaypalEmail() {
        return paypalEmail;
    }

    @JsonIgnore
    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    @JsonIgnore
    public String getSecondLine() {
        return secondLine;
    }

    @JsonIgnore
    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

}
