package com.libereco.springsocial.etsy.api;

import java.util.Arrays;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ListingDeserializer.class)
public class EtsyListing {

    @JsonProperty("listing_id")
    private int listingId;

    @JsonProperty("state")
    private String state;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("category_id")
    private int categoryId;

    @JsonProperty
    private String title;

    @JsonProperty
    private String description;

    @JsonProperty("creation_tsz")
    private Date creationDate;

    @JsonProperty("ending_tsz")
    private Date endingDate;

    @JsonProperty
    private double price;

    @JsonProperty
    private String[] categoryPath;

    @JsonProperty
    private String currencyCode;

    @JsonProperty
    private String[] tags;

    @JsonProperty
    private int quantity;

    @JsonProperty
    private String url;

    @JsonProperty("shipping_template_id")
    private int shippingTemplateId;

    @JsonProperty("who_made")
    private String whoMade;

    @JsonProperty("is_supply")
    private boolean supply;

    @JsonProperty("when_made")
    private String whenMade;

    @JsonIgnore
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonIgnore
    public int getUserId() {
        return userId;
    }

    @JsonIgnore
    public void setState(String state) {
        this.state = state;
    }

    @JsonIgnore
    public String getState() {
        return state;
    }

    @JsonIgnore
    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    @JsonIgnore
    public int getListingId() {
        return listingId;
    }

    @JsonIgnore
    public int getQuantity() {
        return quantity;
    }

    @JsonIgnore
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonIgnore
    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public double getPrice() {
        return price;
    }

    @JsonIgnore
    public void setPrice(double price) {
        this.price = price;
    }

    @JsonIgnore
    public int getShippingTemplateId() {
        return shippingTemplateId;
    }

    @JsonIgnore
    public void setShippingTemplateId(int shippingTemplateId) {
        this.shippingTemplateId = shippingTemplateId;
    }

    @JsonIgnore
    public int getCategoryId() {
        return categoryId;
    }

    @JsonIgnore
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @JsonIgnore
    public String getWhoMade() {
        return whoMade;
    }

    @JsonIgnore
    public void setWhoMade(String whoMade) {
        this.whoMade = whoMade;
    }

    @JsonIgnore
    public boolean isSupply() {
        return supply;
    }

    @JsonIgnore
    public void setSupply(boolean supply) {
        this.supply = supply;
    }

    @JsonIgnore
    public String getWhenMade() {
        return whenMade;
    }

    @JsonIgnore
    public void setWhenMade(String whenMade) {
        this.whenMade = whenMade;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public String[] getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String[] categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Listing [listingId=" + listingId + ", state=" + state + ", userId=" + userId + ", categoryId=" + categoryId + ", title=" + title
                + ", description=" + description + ", creationDate=" + creationDate + ", endingDate=" + endingDate + ", price=" + price
                + ", categoryPath=" + Arrays.toString(categoryPath) + ", currencyCode=" + currencyCode + ", tags=" + Arrays.toString(tags)
                + ", quantity=" + quantity + ", url=" + url + ", shippingTemplateId=" + shippingTemplateId + ", whoMade=" + whoMade + ", supply="
                + supply + ", whenMade=" + whenMade + "]";
    }

    
}
