package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class MarketplaceAuthorizations implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Size(max = 4000)
    private String token;

    private String tokenSecret;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date expirationTime;

    @ManyToOne
    private LiberecoUser liberecoUser;

    @EmbeddedId
    private MarketplaceAuthorizationsCompositeKey key;

    @Version
    @Column(name = "version")
    private Integer version;

    public MarketplaceAuthorizations() {
    }

    public MarketplaceAuthorizationsCompositeKey getKey() {
        return this.key;
    }

    public void setKey(MarketplaceAuthorizationsCompositeKey id) {
        this.key = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return this.tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Date getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public LiberecoUser getLiberecoUser() {
        return this.liberecoUser;
    }

    public void setLiberecoUser(LiberecoUser liberecoUser) {
        this.liberecoUser = liberecoUser;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static MarketplaceAuthorizations fromJsonToMarketplaceAuthorizations(String json) {
        return new JSONDeserializer<MarketplaceAuthorizations>().use(null, MarketplaceAuthorizations.class).deserialize(json);
    }

    public static String toJsonArray(Collection<MarketplaceAuthorizations> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<MarketplaceAuthorizations> fromJsonArrayToMarketplaceAuthorizationses(String json) {
        return new JSONDeserializer<List<MarketplaceAuthorizations>>().use(null, ArrayList.class).use("values", MarketplaceAuthorizations.class)
                .deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
