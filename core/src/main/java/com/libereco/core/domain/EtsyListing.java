package com.libereco.core.domain;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

@Entity
public class EtsyListing implements Serializable, Listing {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    private int listingId;

    @NotNull
    @Enumerated
    private EtsyWhoMade whoMade;

    @NotNull
    private boolean supply;

    @NotNull
    @Enumerated
    private EtsyWhenMade whenMade;

    @NotNull
    @ManyToOne
    private LiberecoListing liberecoListing;

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

    public void setLiberecoListing(LiberecoListing liberecoListing) {
        this.liberecoListing = liberecoListing;
    }

    public LiberecoListing getLiberecoListing() {
        return liberecoListing;
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

    public static EtsyListing fromJsonToEtsyListing(String json) {
        return new JSONDeserializer<EtsyListing>().
                use(null, EtsyListing.class).
                use("liberecoListing", new ObjectFactory() {

                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
                        return LiberecoListing.fromJsonToLiberecoListing((String) value);
                    }
                }).
                deserialize(json);
    }

    public static String toJsonArray(Collection<EtsyListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<EtsyListing> fromJsonArrayToEbayListings(String json) {
        return new JSONDeserializer<List<EtsyListing>>().use(null, ArrayList.class).use("values", EtsyListing.class).deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
