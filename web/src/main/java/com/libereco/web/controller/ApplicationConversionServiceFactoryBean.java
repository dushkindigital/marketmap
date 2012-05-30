package com.libereco.web.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;
import com.libereco.core.service.MarketplaceService;

@Configurable
/**
 * A central place to register application converters and formatters. 
 */
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Autowired
    MarketplaceService marketplaceService;

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
        registry.addConverter(new Converter<String, Marketplace>() {

            @Override
            public Marketplace convert(String id) {
                return marketplaceService.findMarketplace(Long.valueOf(id));
            }
        });

        registry.addConverter(new Converter<Marketplace, String>() {

            @Override
            public String convert(Marketplace marketPlace) {
                return new StringBuilder().append(marketPlace.getMarketplaceName()).toString();
            }
        });
    }

    @Autowired
    LiberecoUserService liberecoUserService;

    @Autowired
    MarketplaceAuthorizationsService marketplaceAuthorizationsService;

    public Converter<LiberecoUser, String> getLiberecoUserToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.libereco.core.domain.LiberecoUser, java.lang.String>() {
            public String convert(LiberecoUser liberecoUser) {
                return new StringBuilder().append(liberecoUser.getUsername()).append(" ").append(liberecoUser.getPassword()).append(" ")
                        .append(liberecoUser.getCreated()).append(" ").append(liberecoUser.getLastUpdated()).toString();
            }
        };
    }

    public Converter<Long, LiberecoUser> getIdToLiberecoUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.libereco.core.domain.LiberecoUser>() {
            public com.libereco.core.domain.LiberecoUser convert(java.lang.Long id) {
                return liberecoUserService.findLiberecoUser(id);
            }
        };
    }

    public Converter<String, LiberecoUser> getStringToLiberecoUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.libereco.core.domain.LiberecoUser>() {
            public com.libereco.core.domain.LiberecoUser convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), LiberecoUser.class);
            }
        };
    }

    public Converter<MarketplaceAuthorizations, String> getMarketplaceAuthorizationsToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.libereco.core.domain.MarketplaceAuthorizations, java.lang.String>() {
            public String convert(MarketplaceAuthorizations marketplaceAuthorizations) {
                return new StringBuilder().append(marketplaceAuthorizations.getToken()).append(" ")
                        .append(marketplaceAuthorizations.getTokenSecret()).append(" ").append(marketplaceAuthorizations.getExpirationTime())
                        .toString();
            }
        };
    }

    public Converter<MarketplaceAuthorizationsCompositeKey, MarketplaceAuthorizations> getIdToMarketplaceAuthorizationsConverter() {
        return new org.springframework.core.convert.converter.Converter<com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey, com.libereco.core.domain.MarketplaceAuthorizations>() {
            public com.libereco.core.domain.MarketplaceAuthorizations convert(com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey id) {
                return marketplaceAuthorizationsService.findMarketplaceAuthorizations(id);
            }
        };
    }

    public Converter<String, MarketplaceAuthorizations> getStringToMarketplaceAuthorizationsConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.libereco.core.domain.MarketplaceAuthorizations>() {
            public com.libereco.core.domain.MarketplaceAuthorizations convert(String id) {
                return getObject().convert(getObject().convert(id, MarketplaceAuthorizationsCompositeKey.class), MarketplaceAuthorizations.class);
            }
        };
    }

    public Converter<String, MarketplaceAuthorizationsCompositeKey> getJsonToMarketplaceAuthorizationsCompositeKeyConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey>() {
            public MarketplaceAuthorizationsCompositeKey convert(String encodedJson) {
                return MarketplaceAuthorizationsCompositeKey.fromJsonToMarketplaceAuthorizationsCompositeKey(new String(Base64
                        .decodeBase64(encodedJson)));
            }
        };
    }

    public Converter<MarketplaceAuthorizationsCompositeKey, String> getMarketplaceAuthorizationsCompositeKeyToJsonConverter() {
        return new org.springframework.core.convert.converter.Converter<com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey, java.lang.String>() {
            public String convert(MarketplaceAuthorizationsCompositeKey marketplaceAuthorizationsCompositeKey) {
                return Base64.encodeBase64URLSafeString(marketplaceAuthorizationsCompositeKey.toJson().getBytes());
            }
        };
    }

    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getLiberecoUserToStringConverter());
        registry.addConverter(getIdToLiberecoUserConverter());
        registry.addConverter(getStringToLiberecoUserConverter());
        registry.addConverter(getMarketplaceAuthorizationsToStringConverter());
        registry.addConverter(getIdToMarketplaceAuthorizationsConverter());
        registry.addConverter(getStringToMarketplaceAuthorizationsConverter());
        registry.addConverter(getJsonToMarketplaceAuthorizationsCompositeKeyConverter());
        registry.addConverter(getMarketplaceAuthorizationsCompositeKeyToJsonConverter());
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}