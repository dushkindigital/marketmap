package com.libereco.web.mergelisting;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ReturnPolicy;

public class EbayListingForm {

    @Enumerated
    private ReturnPolicy returnPolicy;

    @NotNull
    private Integer dispatchTimeMax;

    @NotNull
    private Double startPrice;

    private String paypalEmail;

    private Integer lotSize = 1;

    @Enumerated
    @NotNull
    private ListingDuration listingDuration;

    public ReturnPolicy getReturnPolicy() {
        return returnPolicy;
    }

    public void setReturnPolicy(ReturnPolicy returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    public Integer getDispatchTimeMax() {
        return dispatchTimeMax;
    }

    public void setDispatchTimeMax(Integer dispatchTimeMax) {
        this.dispatchTimeMax = dispatchTimeMax;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public ListingDuration getListingDuration() {
        return listingDuration;
    }

    public void setListingDuration(ListingDuration listingDuration) {
        this.listingDuration = listingDuration;
    }

}
