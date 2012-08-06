package com.libereco.web.controller;

import java.util.HashSet;
import java.util.List;
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
import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.exceptions.GenericLiberecoException;
import com.libereco.core.exceptions.LiberecoResourceNotFoundException;
import com.libereco.core.service.EtsyListingService;
import com.libereco.core.service.LiberecoListingService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.EtsyListingOperations;
import com.libereco.springsocial.etsy.api.ListingBuilder;
import com.libereco.springsocial.etsy.api.impl.EtsyByteArrayResource;
import com.libereco.web.common.MarketplaceName;
import com.libereco.web.security.SecurityUtils;

@Controller
public class EtsyListingController {

    private final EtsyApi etsyApi;
    private final LiberecoListingService liberecoListingService;
    private final EtsyListingService etsyListingService;
    private final MarketplaceService marketplaceService;

    @Autowired
    LiberecoUserService liberecoUserService;

    @Inject
    public EtsyListingController(EtsyApi etsyApi, EtsyListingService etsyListingService, LiberecoListingService liberecoListingService,
            MarketplaceService marketplaceService) {
        this.etsyApi = etsyApi;
        this.etsyListingService = etsyListingService;
        this.liberecoListingService = liberecoListingService;
        this.marketplaceService = marketplaceService;
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
        EtsyListingOperations listingOperations = etsyApi.listingOperations();
        com.libereco.springsocial.etsy.api.EtsyListing createdListing = listingOperations.createListing(toListing(etsyListing));
        populateEtsyListing(etsyListing, createdListing);
        etsyListingService.saveEtsyListing(etsyListing);
        Set<Marketplace> marketplaces = liberecoListing.getMarketplaces();
        marketplaces = marketplaces == null ? new HashSet<Marketplace>() : marketplaces;
        marketplaces.add(marketplace);
        liberecoListing.setMarketplaces(marketplaces);
        liberecoListing.setListingState(ListingState.LISTED);
        liberecoListingService.updateLiberecoListing(liberecoListing);
        uploadEtsyListingImage(etsyListing, createdListing);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(etsyListing.toJson(), headers, HttpStatus.CREATED);
    }

    private void uploadEtsyListingImage(EtsyListing etsyListing, com.libereco.springsocial.etsy.api.EtsyListing createdListing) {
        EtsyListingOperations listingOperations = etsyApi.listingOperations();
        byte[] picture = etsyListing.getLiberecoListing().getPicture();
        if (picture == null) {
            return;
        }
        String uploadListingImageResponse = listingOperations.uploadListingImage(createdListing.getListingId(), new EtsyByteArrayResource(picture,
                etsyListing.getLiberecoListing().getPictureName()));
        System.out.println("UploadListingImage Response ---- " + uploadListingImageResponse);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings/{etsyListingId}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showEtsyJson(@PathVariable("liberecoListingId") Long liberecoListingId,
            @PathVariable("etsyListingId") Long etsyListingId) {
        EtsyListing etsyListing = etsyListingService.findEtsyListing(etsyListingId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (etsyListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(etsyListing.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listEtsyJson(@PathVariable("liberecoListingId") Long liberecoListingId) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EtsyListing> result = etsyListingService.findAllEtsyListings(user.getId());
        return new ResponseEntity<String>(EtsyListing.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings/{etsyListingId}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@PathVariable("liberecoListingId") Long liberecoListingId,
            @PathVariable("etsyListingId") Long etsyListingId, @RequestBody String json) {
        EtsyListing etsyListing = EtsyListing.fromJsonToEtsyListing(json);
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(liberecoListingId);
        etsyListing.setLiberecoListing(liberecoListing);
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.ETSY.getName());
        if (marketplace == null) {
            throw new LiberecoResourceNotFoundException(
                    "You can't create listing on etsy marketplace as there is no marketplace found etsy in our system. Please contact system administrator.");
        }
        EtsyListingOperations listingOperations = etsyApi.listingOperations();
        com.libereco.springsocial.etsy.api.EtsyListing updatedListing = toListing(etsyListing);
        listingOperations.updateListing(updatedListing);
        populateEtsyListing(etsyListing, updatedListing);
        etsyListingService.updateEtsyListing(etsyListing);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(etsyListing.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/etsylistings/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id) {
        EtsyListing etsyListing = etsyListingService.findEtsyListing(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (etsyListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        deleteEtsyListing(etsyListing);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private void deleteEtsyListing(EtsyListing etsyListing) {
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.ETSY.getName());
        if (marketplace == null) {
            throw new GenericLiberecoException("No marketplace found for marketplace ebay");
        }
        LiberecoListing liberecoListing = etsyListing.getLiberecoListing();

        etsyApi.listingOperations().deleteListing(etsyListing.getListingId());
        etsyListingService.deleteEtsyListing(etsyListing);
        liberecoListing.getMarketplaces().remove(marketplace);
        liberecoListingService.updateLiberecoListing(liberecoListing);
    }

    private void populateEtsyListing(EtsyListing etsyListing, com.libereco.springsocial.etsy.api.EtsyListing createdEtsyListing) {
        etsyListing.setListingId(createdEtsyListing.getListingId());
        etsyListing.setCreationDate(createdEtsyListing.getCreationDate());
        etsyListing.setEndingDate(createdEtsyListing.getEndingDate());
        etsyListing.setEtsyListingUrl(createdEtsyListing.getUrl());
        etsyListing.setEtsyUserId(createdEtsyListing.getUserId());
        etsyListing.setShippingTemplateId(createdEtsyListing.getShippingTemplateId());
    }

    private com.libereco.springsocial.etsy.api.EtsyListing toListing(EtsyListing etsyListing) {
        LiberecoListing liberecoListing = etsyListing.getLiberecoListing();
        com.libereco.springsocial.etsy.api.EtsyListing listing = ListingBuilder.listing().
                withShippingTemplateId(260).
                withDescription(liberecoListing.getDescription()).
                withPrice(liberecoListing.getPrice()).
                withTitle(liberecoListing.getName()).
                withSupply(etsyListing.isSupply()).
                withQuantity(liberecoListing.getQuantity()).
                withWhenMade(etsyListing.getWhenMade().getValue()).
                withWhoMade(etsyListing.getWhoMade().getValue()).
                withCategoryId(69150467).
                withListingId(etsyListing.getListingId()).
                build();
        return listing;
    }

}
