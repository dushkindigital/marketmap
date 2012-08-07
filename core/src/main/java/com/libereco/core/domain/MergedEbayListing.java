package com.libereco.core.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

@Embeddable
public class MergedEbayListing{


    @Enumerated
    private ReturnPolicy returnPolicy;

    private Integer dispatchTimeMax;

    private Double startPrice;

    private String paypalEmail;

    private Integer lotSize = 0;

    @Enumerated
    private ListingDuration listingDuration;

    private String ebayItemUrl;

    private String ebayItemId;

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

    public String getPaypalEmail() {
        return this.paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public Integer getLotSize() {
        return this.lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    

    public void setListingDuration(ListingDuration listingDuration) {
        this.listingDuration = listingDuration;
    }

    public ListingDuration getListingDuration() {
        return listingDuration;
    }

    public void setEbayItemUrl(String ebayItemUrl) {
        this.ebayItemUrl = ebayItemUrl;
    }

    public String getEbayItemUrl() {
        return ebayItemUrl;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static MergedEbayListing fromJsonToEbayListing(String json) {
        return new JSONDeserializer<MergedEbayListing>().
                use(null, MergedEbayListing.class).
                use("liberecoListing", new ObjectFactory() {

                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
                        return LiberecoListing.fromJsonToLiberecoListing((String) value);
                    }
                }).
                deserialize(json);
    }

    public static String toJsonArray(Collection<MergedEbayListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<MergedEbayListing> fromJsonArrayToEbayListings(String json) {
        return new JSONDeserializer<List<MergedEbayListing>>().use(null, ArrayList.class).use("values", MergedEbayListing.class).deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public void setEbayItemId(String ebayItemId) {
        this.ebayItemId = ebayItemId;
    }

    public String getEbayItemId() {
        return ebayItemId;
    }

}
