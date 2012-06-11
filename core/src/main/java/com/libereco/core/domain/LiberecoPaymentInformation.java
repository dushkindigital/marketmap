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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class LiberecoPaymentInformation implements Serializable {

    @NotNull
    @Enumerated
    private PaymentMethod paymentMethod;

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

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

    public static LiberecoPaymentInformation fromJsonToLiberecoPaymentInformation(String json) {
        return new JSONDeserializer<LiberecoPaymentInformation>().use(null, LiberecoPaymentInformation.class).deserialize(json);
    }

    public static String toJsonArray(Collection<LiberecoPaymentInformation> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<LiberecoPaymentInformation> fromJsonArrayToLiberecoPaymentInformations(String json) {
        return new JSONDeserializer<List<LiberecoPaymentInformation>>().use(null, ArrayList.class).use("values", LiberecoPaymentInformation.class)
                .deserialize(json);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LiberecoPaymentInformation)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        LiberecoPaymentInformation rhs = (LiberecoPaymentInformation) obj;
        return new EqualsBuilder().append(id, rhs.id).append(paymentMethod, rhs.paymentMethod).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(id).append(paymentMethod).toHashCode();
    }

    private static final long serialVersionUID = 1L;
}
