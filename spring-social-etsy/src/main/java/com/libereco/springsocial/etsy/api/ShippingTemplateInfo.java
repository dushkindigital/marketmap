package com.libereco.springsocial.etsy.api;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingTemplateInfo {

    @JsonProperty("shipping_template_id")
    @JsonIgnore
    private int shippingTemplateId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("origin_country_id")
    private int originCountryId;

    @JsonProperty("destination_country_id")
    private int destinationCountryId;

    @JsonProperty("primary_cost")
    private float primaryCost;

    @JsonProperty("secondary_cost")
    private float secondaryCost;

    @JsonIgnore
    public String getTitle() {
        return title;
    }
    @JsonIgnore
    public void setTitle(String title) {
        this.title = title;
    }
    @JsonIgnore
    public int getOriginCountryId() {
        return originCountryId;
    }
    @JsonIgnore
    public void setOriginCountryId(int originCountryId) {
        this.originCountryId = originCountryId;
    }
    @JsonIgnore
    public int getDestinationCountryId() {
        return destinationCountryId;
    }
    @JsonIgnore
    public void setDestinationCountryId(int destinationCountryId) {
        this.destinationCountryId = destinationCountryId;
    }
    @JsonIgnore
    public float getPrimaryCost() {
        return primaryCost;
    }
    @JsonIgnore
    public void setPrimaryCost(float primaryCost) {
        this.primaryCost = primaryCost;
    }
    @JsonIgnore
    public float getSecondaryCost() {
        return secondaryCost;
    }
    @JsonIgnore
    public void setSecondaryCost(float secondaryCost) {
        this.secondaryCost = secondaryCost;
    }
    @JsonIgnore
    public int getShippingTemplateId() {
        return shippingTemplateId;
    }
    @JsonIgnore
    public void setShippingTemplateId(int shippingTemplateId) {
        this.shippingTemplateId = shippingTemplateId;
    }

    @Override
    public String toString() {
        return "ShippingTemplateInfo [shippingTemplateId=" + shippingTemplateId + ", title=" + title + ", originCountryId=" + originCountryId
                + ", destinationCountryId=" + destinationCountryId + ", primaryCost=" + primaryCost + ", secondaryCost=" + secondaryCost + "]";
    }

}
