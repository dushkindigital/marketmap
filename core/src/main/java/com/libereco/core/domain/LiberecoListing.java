package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class LiberecoListing implements Serializable{

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Marketplace> marketplaces = new HashSet<Marketplace>();

    @NotNull
    private Long userId;

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @Value("1")
    private int quantity;

    private String description;

    @NotNull
    @Enumerated
    private LiberecoCategory category;

    @NotNull
    @Enumerated
    private ListingCondition listingCondition;

    @Enumerated
    private ListingState listingState;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date listingDuration;

    private byte[] picture;

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

	public Date getListingDuration() {
        return this.listingDuration;
    }

	public void setListingDuration(Date listingDuration) {
        this.listingDuration = listingDuration;
    }

	public byte[] getPicture() {
        return this.picture;
    }

	public void setPicture(byte[] picture) {
        this.picture = picture;
    }

	private static final long serialVersionUID = 1L;

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

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

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
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
}
