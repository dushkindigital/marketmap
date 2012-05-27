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
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class LiberecoUser implements Serializable {

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Size(max = 64)
    private String password;

    @NotNull
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date created = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date lastUpdated;

    @NotNull
    @Enumerated
    private UserStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "liberecoUser")
    private Set<MarketplaceAuthorizations> marketplaceAuthorizations = new HashSet<MarketplaceAuthorizations>();

    public LiberecoUser() {
        // TODO Auto-generated constructor stub
    }

    public LiberecoUser(String username,String password) {
        this.username = username;
        this.password = password;
        this.lastUpdated = new Date();
        this.status = UserStatus.ACTIVE;
    }
    
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<MarketplaceAuthorizations> getMarketplaceAuthorizations() {
        return this.marketplaceAuthorizations;
    }

    public void setMarketplaceAuthorizations(Set<MarketplaceAuthorizations> marketplaceAuthorizations) {
        this.marketplaceAuthorizations = marketplaceAuthorizations;
    }

    private static final long serialVersionUID = 1L;

    public boolean equals(Object obj) {
        if (!(obj instanceof LiberecoUser)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        LiberecoUser rhs = (LiberecoUser) obj;
        return new EqualsBuilder().append(created, rhs.created).append(id, rhs.id).append(lastUpdated, rhs.lastUpdated)
                .append(password, rhs.password).append(status, rhs.status).append(username, rhs.username).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(created).append(id).append(lastUpdated).append(password).append(status).append(username).toHashCode();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static LiberecoUser fromJsonToLiberecoUser(String json) {
        return new JSONDeserializer<LiberecoUser>().use(null, LiberecoUser.class).deserialize(json);
    }

    public static String toJsonArray(Collection<LiberecoUser> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<LiberecoUser> fromJsonArrayToLiberecoUsers(String json) {
        return new JSONDeserializer<List<LiberecoUser>>().use(null, ArrayList.class).use("values", LiberecoUser.class).deserialize(json);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
