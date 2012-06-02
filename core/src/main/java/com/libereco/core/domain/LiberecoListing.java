package com.libereco.core.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
@RooJson
public class LiberecoListing {

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
}
