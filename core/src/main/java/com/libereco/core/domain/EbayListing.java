package com.libereco.core.domain;

import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
@RooJson
public class EbayListing {

    @Enumerated
    private ReturnPolicy returnPolicy;

    private Integer dispatchTimeMax;

    private Double startPrice;

    private Double reservePrice;

    private Double buyItNowPrice;

    private Float vatPercent;

    private String paypalEmail;

    private Boolean borderChecked;

    private Boolean boldTitleChecked;

    private Boolean autoPay;

    private Integer lotSize;

    private Boolean bestOfferEnabled;

    @NotNull
    @ManyToOne
    private LiberecoListing liberecoListing;
}
