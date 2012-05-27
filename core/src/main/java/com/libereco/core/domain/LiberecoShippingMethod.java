package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

@Entity
public class LiberecoShippingMethod implements Serializable {

    private String name;

    @Enumerated
    private ShippingLevelType shippingLevelType;

    @NotNull
    @ManyToOne
    private Marketplace marketplace;

    private static final long serialVersionUID = 1L;

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShippingLevelType getShippingLevelType() {
        return this.shippingLevelType;
    }

    public void setShippingLevelType(ShippingLevelType shippingLevelType) {
        this.shippingLevelType = shippingLevelType;
    }

    public Marketplace getMarketplace() {
        return this.marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static LiberecoShippingMethod fromJsonToLiberecoShippingMethod(String json) {
        return new JSONDeserializer<LiberecoShippingMethod>().use(null, LiberecoShippingMethod.class).deserialize(json);
    }

    public static String toJsonArray(Collection<LiberecoShippingMethod> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<LiberecoShippingMethod> fromJsonArrayToLiberecoShippingMethods(String json) {
        return new JSONDeserializer<List<LiberecoShippingMethod>>().use(null, ArrayList.class).use("values", LiberecoShippingMethod.class)
                .deserialize(json);
    }
}
