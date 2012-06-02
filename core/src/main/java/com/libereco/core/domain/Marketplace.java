package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Marketplace implements Serializable {

    @NotNull
    @Column(unique = true)
    private String marketplaceName;

    private String marketplaceShortName;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<LiberecoShippingMethod> shippingMethods = new HashSet<LiberecoShippingMethod>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    public Marketplace() {
        // TODO Auto-generated constructor stub
    }

    public Marketplace(String marketplaceName, String marketplaceShortName) {
        this.marketplaceName = marketplaceName;
        this.marketplaceShortName = marketplaceShortName;
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

    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "shippingMethods");
    }

    private static final long serialVersionUID = 1L;

    public String getMarketplaceName() {
        return this.marketplaceName;
    }

    public void setMarketplaceName(String marketplaceName) {
        this.marketplaceName = marketplaceName;
    }

    public String getMarketplaceShortName() {
        return this.marketplaceShortName;
    }

    public void setMarketplaceShortName(String marketplaceShortName) {
        this.marketplaceShortName = marketplaceShortName;
    }

    public Set<LiberecoShippingMethod> getShippingMethods() {
        return this.shippingMethods;
    }

    public void setShippingMethods(Set<LiberecoShippingMethod> shippingMethods) {
        this.shippingMethods = shippingMethods;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static Marketplace fromJsonToMarketplace(String json) {
        return new JSONDeserializer<Marketplace>().use(null, Marketplace.class).deserialize(json);
    }

    public static String toJsonArray(Collection<Marketplace> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<Marketplace> fromJsonArrayToMarketplaces(String json) {
        return new JSONDeserializer<List<Marketplace>>().use(null, ArrayList.class).use("values", Marketplace.class).deserialize(json);
    }
}
