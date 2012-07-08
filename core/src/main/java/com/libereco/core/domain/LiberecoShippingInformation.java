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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class LiberecoShippingInformation implements Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    @Enumerated
    private ShippingType shippingType;

    @NotNull
    @Enumerated
    private ShippingService shippingService;

    @NotNull
    private double shippingCost;

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

    public ShippingType getShippingType() {
        return this.shippingType;
    }

    public void setShippingType(ShippingType shippingType) {
        this.shippingType = shippingType;
    }

    public ShippingService getShippingService() {
        return this.shippingService;
    }

    public void setShippingService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public double getShippingCost() {
        return this.shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static LiberecoShippingInformation fromJsonToLiberecoShippingInformation(String json) {
        return new JSONDeserializer<LiberecoShippingInformation>().use(null, LiberecoShippingInformation.class).deserialize(json);
    }

    public static String toJsonArray(Collection<LiberecoShippingInformation> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<LiberecoShippingInformation> fromJsonArrayToLiberecoShippingInformations(String json) {
        return new JSONDeserializer<List<LiberecoShippingInformation>>().use(null, ArrayList.class).use("values", LiberecoShippingInformation.class).deserialize(json);
    }

}
