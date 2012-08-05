package com.libereco.web.mergelisting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.libereco.core.domain.ItemLocation;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;

public class LiberecoListingForm implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    private int quantity = 1;

    private String description;

    @NotNull
    @Enumerated
    private LiberecoCategory category;

    @NotNull
    @Enumerated
    private ListingCondition listingCondition;

    @Enumerated
    private ListingState listingState;

    private PictureForm pictureForm;

    private EbayListingForm ebayListingForm;

    private EtsyListingForm etsyListingForm;

    @Embedded
    @NotNull
    private ItemLocation itemLocation;

    @NotNull
    private List<LiberecoShippingInformation> shippingInformations = new ArrayList<LiberecoShippingInformation>();

    @NotNull
    private List<LiberecoPaymentInformation> liberecoPaymentInformations = new ArrayList<LiberecoPaymentInformation>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LiberecoCategory getCategory() {
        return this.category;
    }

    public void setCategory(LiberecoCategory category) {
        this.category = category;
    }

    public ListingCondition getListingCondition() {
        return this.listingCondition;
    }

    public void setListingCondition(ListingCondition listingCondition) {
        this.listingCondition = listingCondition;
    }

    public ListingState getListingState() {
        return this.listingState;
    }

    public void setListingState(ListingState listingState) {
        this.listingState = listingState;
    }

    public void setPictureForm(PictureForm pictureForm) {
        this.pictureForm = pictureForm;
    }

    public PictureForm getPictureForm() {
        return pictureForm;
    }

    public void setItemLocation(ItemLocation itemLocation) {
        this.itemLocation = itemLocation;
    }

    public ItemLocation getItemLocation() {
        return itemLocation;
    }

    public void setShippingInformations(List<LiberecoShippingInformation> shippingInformations) {
        this.shippingInformations = shippingInformations;
    }

    public List<LiberecoShippingInformation> getShippingInformations() {
        return shippingInformations;
    }

    public void setLiberecoPaymentInformations(List<LiberecoPaymentInformation> liberecoPaymentInformations) {
        this.liberecoPaymentInformations = liberecoPaymentInformations;
    }

    public List<LiberecoPaymentInformation> getLiberecoPaymentInformations() {
        return liberecoPaymentInformations;
    }

    public void setEtsyListingForm(EtsyListingForm etsyListingForm) {
        this.etsyListingForm = etsyListingForm;
    }

    public EtsyListingForm getEtsyListingForm() {
        return etsyListingForm;
    }

    public void setEbayListingForm(EbayListingForm ebayListingForm) {
        this.ebayListingForm = ebayListingForm;
    }

    public EbayListingForm getEbayListingForm() {
        return ebayListingForm;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
