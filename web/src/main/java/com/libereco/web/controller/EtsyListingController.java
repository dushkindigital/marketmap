package com.libereco.web.controller;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.libereco.core.domain.EtsyListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.exceptions.LiberecoResourceNotFoundException;
import com.libereco.core.service.EtsyListingService;
import com.libereco.core.service.LiberecoListingService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.Listing;
import com.libereco.springsocial.etsy.api.ListingBuilder;
import com.libereco.web.common.MarketplaceName;

@Controller
public class EtsyListingController {

    private final EtsyApi etsyApi;
    private final LiberecoListingService liberecoListingService;
    private final EtsyListingService etsyListingService;
    @Autowired
    MarketplaceService marketplaceService;

    @Inject
    public EtsyListingController(EtsyApi etsyApi, EtsyListingService etsyListingService, LiberecoListingService liberecoListingService) {
        this.etsyApi = etsyApi;
        this.etsyListingService = etsyListingService;
        this.liberecoListingService = liberecoListingService;
    }

    @RequestMapping(value = "/liberecolistings/{libercoListingId}/etsylistings", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createEtsyListingFromJson(@PathVariable("libercoListingId") Long liberecoListingId, @RequestBody String json) {
        EtsyListing etsyListing = EtsyListing.fromJsonToEtsyListing(json);
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(liberecoListingId);
        etsyListing.setLiberecoListing(liberecoListing);
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.ETSY.getName());
        if (marketplace == null) {
            throw new LiberecoResourceNotFoundException(
                    "You can't create listing on etsy marketplace as there is no marketplace found etsy in our system. Please contact system administrator.");
        }
        etsyApi.listingOperations().createListing(toListing(etsyListing));
        etsyListingService.saveEtsyListing(etsyListing);
        Set<Marketplace> marketplaces = liberecoListing.getMarketplaces();
        marketplaces = marketplaces == null ? new HashSet<Marketplace>() : marketplaces;
        marketplaces.add(marketplace);
        liberecoListing.setMarketplaces(marketplaces);
        liberecoListing.setListingState(ListingState.LISTED);
        liberecoListingService.updateLiberecoListing(liberecoListing);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(etsyListing.toJson(), headers, HttpStatus.CREATED);
    }

    private Listing toListing(EtsyListing etsyListing) {
        LiberecoListing liberecoListing = etsyListing.getLiberecoListing();
        Listing listing = ListingBuilder.listing().
                withShippingTemplateId(260).
                withDescription(liberecoListing.getDescription()).
                withPrice(liberecoListing.getPrice()).
                withTitle(liberecoListing.getName()).
                withSupply(true).
                withQuantity(liberecoListing.getQuantity()).
                withWhenMade(etsyListing.getWhenMade().getValue()).
                withWhoMade(etsyListing.getWhoMade().getValue()).
                withCategoryId(69150467).
                build();
        return listing;
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings/{etsyListingId}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showEtsyJson(@PathVariable("liberecoListingId") Long liberecoListingId,
            @PathVariable("etsyListingId") Long etsyListingId) {
        return null;
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listEtsyJson(@PathVariable("liberecoListingId") Long liberecoListingId) {
        return null;
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings/{etsyListingId}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@PathVariable("liberecoListingId") Long liberecoListingId,
            @PathVariable("etsyListingId") Long etsyListingId, @RequestBody String json) {
        return null;
    }

}
