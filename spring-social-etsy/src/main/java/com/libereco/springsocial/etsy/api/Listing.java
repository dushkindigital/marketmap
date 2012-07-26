package com.libereco.springsocial.etsy.api;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Listing {

    @JsonProperty
    private int quantity;

    @JsonProperty
    private String title;

    @JsonProperty
    private String description;

    @JsonProperty
    private double price;

    @JsonProperty("shipping_template_id")
    private int shippingTemplateId;

    @JsonProperty("category_id")
    private int categoryId;

    @JsonProperty("who_made")
    private String whoMade;

    @JsonProperty("is_supply")
    private boolean supply;

    @JsonProperty("when_made")
    private String whenMade;

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

}
