package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class LiberecoListing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Marketplace> marketplaces = new HashSet<Marketplace>();

    private Long userId;

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @Value("1")
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

    @Lob
    private byte[] picture;

    private String pictureName;

    @Version
    @Column(name = "version")
    private Integer version;

    private String pictureUrl;

    @Embedded
    @NotNull
    private ItemLocation itemLocation;

    @NotNull
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<LiberecoShippingInformation> shippingInformations;

    @NotNull
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<LiberecoPaymentInformation> liberecoPaymentInformations;

    public LiberecoListing() {
        this.shippingInformations = new ArrayList<LiberecoShippingInformation>();
    }

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

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public Set<Marketplace> getMarketplaces() {
        return this.marketplaces;
    }

    public void setMarketplaces(Set<Marketplace> marketplaces) {
        this.marketplaces = marketplaces;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public byte[] getPicture() {
        return this.picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
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

    public String toJson() {
        return new JSONSerializer().exclude("*.class").include("shippingInformations").include("liberecoPaymentInformations").serialize(this);
    }

    public static LiberecoListing fromJsonToLiberecoListing(String json) {
        return new JSONDeserializer<LiberecoListing>().use(null, LiberecoListing.class).deserialize(json);
    }

    public static String toJsonArray(Collection<LiberecoListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<LiberecoListing> fromJsonArrayToLiberecoListings(String json) {
        return new JSONDeserializer<List<LiberecoListing>>().use(null, ArrayList.class).use("values", LiberecoListing.class).deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
