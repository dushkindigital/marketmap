// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.libereco.springsocial.etsy.api;

public class PaymentTemplateInfoBuilder extends PaymentTemplateInfoBuilderBase<PaymentTemplateInfoBuilder> {
    public static PaymentTemplateInfoBuilder paymentTemplateInfo() {
        return new PaymentTemplateInfoBuilder();
    }

    public PaymentTemplateInfoBuilder() {
        super(new PaymentTemplateInfo());
    }

    public PaymentTemplateInfo build() {
        return getInstance();
    }
}

class PaymentTemplateInfoBuilderBase<GeneratorT extends PaymentTemplateInfoBuilderBase<GeneratorT>> {
    private PaymentTemplateInfo instance;

    protected PaymentTemplateInfoBuilderBase(PaymentTemplateInfo aInstance) {
        instance = aInstance;
    }

    protected PaymentTemplateInfo getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAllowCheck(boolean aValue) {
        instance.setAllowCheck(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAllowMo(boolean aValue) {
        instance.setAllowMo(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withState(String aValue) {
        instance.setState(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withZip(String aValue) {
        instance.setZip(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAllowOther(boolean aValue) {
        instance.setAllowOther(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAllowPaypal(boolean aValue) {
        instance.setAllowPaypal(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCity(String aValue) {
        instance.setCity(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCountryId(int aValue) {
        instance.setCountryId(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withFirstLine(String aValue) {
        instance.setFirstLine(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withName(String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withPaypalEmail(String aValue) {
        instance.setPaypalEmail(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withSecondLine(String aValue) {
        instance.setSecondLine(aValue);

        return (GeneratorT) this;
    }
}
