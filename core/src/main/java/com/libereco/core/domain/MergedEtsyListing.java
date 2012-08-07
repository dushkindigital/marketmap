package com.libereco.core.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
public class MergedEtsyListing {

    private int listingId;

    @Enumerated
    private EtsyWhoMade whoMade;

    private boolean supply;

    @Enumerated
    private EtsyWhenMade whenMade;

    private String etsyListingUrl;

    private Date creationDate;

    private Date endingDate;

    private int shippingTemplateId;

    private int etsyUserId;

    public void setEtsyUserId(int etsyUserId) {
        this.etsyUserId = etsyUserId;
    }

    public int getEtsyUserId() {
        return etsyUserId;
    }

    public void setShippingTemplateId(int shippingTemplateId) {
        this.shippingTemplateId = shippingTemplateId;
    }

    public int getShippingTemplateId() {
        return shippingTemplateId;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEtsyListingUrl(String etsyListingUrl) {
        this.etsyListingUrl = etsyListingUrl;
    }

    public String getEtsyListingUrl() {
        return etsyListingUrl;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public EtsyWhoMade getWhoMade() {
        return whoMade;
    }

    public void setWhoMade(EtsyWhoMade whoMade) {
        this.whoMade = whoMade;
    }

    public boolean isSupply() {
        return supply;
    }

    public void setSupply(boolean supply) {
        this.supply = supply;
    }

    public EtsyWhenMade getWhenMade() {
        return whenMade;
    }

    public void setWhenMade(EtsyWhenMade whenMade) {
        this.whenMade = whenMade;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static MergedEtsyListing fromJsonToEtsyListing(String json) {
        return new JSONDeserializer<MergedEtsyListing>().
                use(null, MergedEtsyListing.class).
                use("liberecoListing", new ObjectFactory() {

                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
                        return LiberecoListing.fromJsonToLiberecoListing((String) value);
                    }
                }).
                deserialize(json);
    }

    public static String toJsonArray(Collection<MergedEtsyListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<MergedEtsyListing> fromJsonArrayToEbayListings(String json) {
        return new JSONDeserializer<List<MergedEtsyListing>>().use(null, ArrayList.class).use("values", MergedEtsyListing.class).deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
