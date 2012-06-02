// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.libereco.core.domain;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ReturnPolicy;

privileged aspect EbayListing_Roo_JavaBean {
    
    public ReturnPolicy EbayListing.getReturnPolicy() {
        return this.returnPolicy;
    }
    
    public void EbayListing.setReturnPolicy(ReturnPolicy returnPolicy) {
        this.returnPolicy = returnPolicy;
    }
    
    public Integer EbayListing.getDispatchTimeMax() {
        return this.dispatchTimeMax;
    }
    
    public void EbayListing.setDispatchTimeMax(Integer dispatchTimeMax) {
        this.dispatchTimeMax = dispatchTimeMax;
    }
    
    public Double EbayListing.getStartPrice() {
        return this.startPrice;
    }
    
    public void EbayListing.setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }
    
    public Double EbayListing.getReservePrice() {
        return this.reservePrice;
    }
    
    public void EbayListing.setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }
    
    public Double EbayListing.getBuyItNowPrice() {
        return this.buyItNowPrice;
    }
    
    public void EbayListing.setBuyItNowPrice(Double buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }
    
    public Float EbayListing.getVatPercent() {
        return this.vatPercent;
    }
    
    public void EbayListing.setVatPercent(Float vatPercent) {
        this.vatPercent = vatPercent;
    }
    
    public String EbayListing.getPaypalEmail() {
        return this.paypalEmail;
    }
    
    public void EbayListing.setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }
    
    public Boolean EbayListing.getBorderChecked() {
        return this.borderChecked;
    }
    
    public void EbayListing.setBorderChecked(Boolean borderChecked) {
        this.borderChecked = borderChecked;
    }
    
    public Boolean EbayListing.getBoldTitleChecked() {
        return this.boldTitleChecked;
    }
    
    public void EbayListing.setBoldTitleChecked(Boolean boldTitleChecked) {
        this.boldTitleChecked = boldTitleChecked;
    }
    
    public Boolean EbayListing.getAutoPay() {
        return this.autoPay;
    }
    
    public void EbayListing.setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }
    
    public Integer EbayListing.getLotSize() {
        return this.lotSize;
    }
    
    public void EbayListing.setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }
    
    public Boolean EbayListing.getBestOfferEnabled() {
        return this.bestOfferEnabled;
    }
    
    public void EbayListing.setBestOfferEnabled(Boolean bestOfferEnabled) {
        this.bestOfferEnabled = bestOfferEnabled;
    }
    
    public LiberecoListing EbayListing.getLiberecoListing() {
        return this.liberecoListing;
    }
    
    public void EbayListing.setLiberecoListing(LiberecoListing liberecoListing) {
        this.liberecoListing = liberecoListing;
    }
    
}
