package com.libereco.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Embeddable
public final class MarketplaceAuthorizationsCompositeKey implements Serializable {

    private Long userId;

    private Long marketplaceId;

    private static final long serialVersionUID = 1L;

    public MarketplaceAuthorizationsCompositeKey(Long userId, Long marketplaceId) {
        super();
        this.userId = userId;
        this.marketplaceId = marketplaceId;
    }

    public MarketplaceAuthorizationsCompositeKey() {
        super();
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MarketplaceAuthorizationsCompositeKey)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MarketplaceAuthorizationsCompositeKey rhs = (MarketplaceAuthorizationsCompositeKey) obj;
        return new EqualsBuilder().append(marketplaceId, rhs.marketplaceId).append(userId, rhs.userId).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(marketplaceId).append(userId).toHashCode();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static MarketplaceAuthorizationsCompositeKey fromJsonToMarketplaceAuthorizationsCompositeKey(String json) {
        return new JSONDeserializer<MarketplaceAuthorizationsCompositeKey>().use(null, MarketplaceAuthorizationsCompositeKey.class).deserialize(json);
    }

    public static String toJsonArray(Collection<MarketplaceAuthorizationsCompositeKey> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<MarketplaceAuthorizationsCompositeKey> fromJsonArrayToMarketplaceAuthorizationsCompositeKeys(String json) {
        return new JSONDeserializer<List<MarketplaceAuthorizationsCompositeKey>>().use(null, ArrayList.class)
                .use("values", MarketplaceAuthorizationsCompositeKey.class).deserialize(json);
    }
}
