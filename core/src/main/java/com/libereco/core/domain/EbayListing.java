package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class EbayListing implements Serializable{

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

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static EbayListing fromJsonToEbayListing(String json) {
        return new JSONDeserializer<EbayListing>().use(null, EbayListing.class).deserialize(json);
    }

	public static String toJsonArray(Collection<EbayListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<EbayListing> fromJsonArrayToEbayListings(String json) {
        return new JSONDeserializer<List<EbayListing>>().use(null, ArrayList.class).use("values", EbayListing.class).deserialize(json);
    }

	public ReturnPolicy getReturnPolicy() {
        return this.returnPolicy;
    }

	public void setReturnPolicy(ReturnPolicy returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

	public Integer getDispatchTimeMax() {
        return this.dispatchTimeMax;
    }

	public void setDispatchTimeMax(Integer dispatchTimeMax) {
        this.dispatchTimeMax = dispatchTimeMax;
    }

	public Double getStartPrice() {
        return this.startPrice;
    }

	public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

	public Double getReservePrice() {
        return this.reservePrice;
    }

	public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

	public Double getBuyItNowPrice() {
        return this.buyItNowPrice;
    }

	public void setBuyItNowPrice(Double buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }

	public Float getVatPercent() {
        return this.vatPercent;
    }

	public void setVatPercent(Float vatPercent) {
        this.vatPercent = vatPercent;
    }

	public String getPaypalEmail() {
        return this.paypalEmail;
    }

	public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

	public Boolean getBorderChecked() {
        return this.borderChecked;
    }

	public void setBorderChecked(Boolean borderChecked) {
        this.borderChecked = borderChecked;
    }

	public Boolean getBoldTitleChecked() {
        return this.boldTitleChecked;
    }

	public void setBoldTitleChecked(Boolean boldTitleChecked) {
        this.boldTitleChecked = boldTitleChecked;
    }

	public Boolean getAutoPay() {
        return this.autoPay;
    }

	public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

	public Integer getLotSize() {
        return this.lotSize;
    }

	public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

	public Boolean getBestOfferEnabled() {
        return this.bestOfferEnabled;
    }

	public void setBestOfferEnabled(Boolean bestOfferEnabled) {
        this.bestOfferEnabled = bestOfferEnabled;
    }

	public LiberecoListing getLiberecoListing() {
        return this.liberecoListing;
    }

	public void setLiberecoListing(LiberecoListing liberecoListing) {
        this.liberecoListing = liberecoListing;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }
}
