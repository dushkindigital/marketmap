package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.DelistingReason;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.exceptions.GenericLiberecoException;
import com.libereco.core.exceptions.LiberecoResourceNotFoundException;
import com.libereco.core.exceptions.UserMarketplaceAuthorizationException;
import com.libereco.core.service.EbayListingService;
import com.libereco.core.service.LiberecoListingService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.web.common.MarketplaceName;
import com.libereco.web.external.ebay.EbayClient;
import com.libereco.web.security.SecurityUtils;

@Controller
public class EbayListingController {

    @Autowired
    EbayListingService ebayListingService;

    @Autowired
    LiberecoListingService liberecoListingService;

    @Autowired
    EbayClient ebayClient;

    @Autowired
    MarketplaceAuthorizationsService marketplaceAuthorizationsService;

    @Autowired
    MarketplaceService marketplaceService;

    @Autowired
    LiberecoUserService liberecoUserService;

    @RequestMapping(value = "/ebaylistings", method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EbayListing ebayListing, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, ebayListing);
            return "ebaylistings/create";
        }
        uiModel.asMap().clear();
        createEbayListing(ebayListing);
        return "redirect:/liberecolistings/" + encodeUrlPathSegment(ebayListing.getLiberecoListing().getId().toString(), httpServletRequest)
                + "/ebaylistings/" + encodeUrlPathSegment(ebayListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/liberecolistings/{libercoListingId}/ebaylistings", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@PathVariable("libercoListingId") Long liberecoListingId, @RequestBody String json) {
        EbayListing ebayListing = EbayListing.fromJsonToEbayListing(json);
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(liberecoListingId);
        ebayListing.setLiberecoListing(liberecoListing);
        createEbayListing(ebayListing);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(ebayListing.toJson(), headers, HttpStatus.CREATED);
    }

    private void createEbayListing(EbayListing ebayListing) {
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.EBAY.getName());
        if (marketplace == null) {
            throw new LiberecoResourceNotFoundException(
                    "You can't create listing on Ebay marketplace as there is no marketplace found ebay in our system. Please contact system administrator.");
        }
        LiberecoListing liberecoListing = ebayListing.getLiberecoListing();
        MarketplaceAuthorizations ebayAuthorization = marketplaceAuthorizationsService
                .findMarketplaceAuthorizations(new MarketplaceAuthorizationsCompositeKey(liberecoListing.getUserId(), marketplace.getId()));
        if (ebayAuthorization == null) {
            throw new UserMarketplaceAuthorizationException(MarketplaceName.EBAY.getName());
        }
        ebayClient.addListing(ebayListing, ebayAuthorization.getToken());
        ebayListingService.saveEbayListing(ebayListing);
        Set<Marketplace> marketplaces = liberecoListing.getMarketplaces();
        marketplaces = marketplaces == null ? new HashSet<Marketplace>() : marketplaces;
        marketplaces.add(marketplace);
        liberecoListing.setMarketplaces(marketplaces);
        liberecoListing.setListingState(ListingState.LISTED);
        liberecoListingService.updateLiberecoListing(liberecoListing);
    }

    @RequestMapping(value = "/ebaylistings", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new EbayListing());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (liberecoListingService.countAllLiberecoListings() == 0) {
            dependencies.add(new String[] { "liberecolisting", "liberecolistings" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "ebaylistings/create";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", produces = "text/html")
    public String show(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("ebaylisting", ebayListingService.findEbayListing(id));
        uiModel.addAttribute("itemId", id);
        return "ebaylistings/show";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id) {
        EbayListing ebayListing = ebayListingService.findEbayListing(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (ebayListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(ebayListing.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/ebaylistings", produces = "text/html")
    public String listAll(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("ebaylistings", ebayListingService.findEbayListingEntries(user.getId(), firstResult, sizeNo));
            float nrOfPages = (float) ebayListingService.countAllEbayListings() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("ebaylistings", ebayListingService.findAllEbayListings(user.getId()));
        }
        return "ebaylistings/list";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings", produces = "text/html")
    public String list(@PathVariable("liberecoListingId") Long liberecoListingId, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("ebaylistings", ebayListingService.findEbayListingEntries(user.getId(), firstResult, sizeNo));
            float nrOfPages = (float) ebayListingService.countAllEbayListings() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("ebaylistings", ebayListingService.findAllEbayListings(user.getId()));
        }
        return "ebaylistings/list";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(@PathVariable("liberecoListingId") Long liberecoListingId) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EbayListing> result = ebayListingService.findAllEbayListings(user.getId());
        return new ResponseEntity<String>(EbayListing.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id, @Valid EbayListing ebayListing,
            BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, ebayListing);
            return "ebaylistings/update";
        }
        uiModel.asMap().clear();
        updateEbayListing(ebayListing);
        return "redirect:/liberecolistings/" + encodeUrlPathSegment(ebayListing.getLiberecoListing().getId().toString(), httpServletRequest)
                + "/ebaylistings/" + encodeUrlPathSegment(ebayListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id,
            @RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EbayListing ebayListing = EbayListing.fromJsonToEbayListing(json);
        ebayListing.setEbayItemId(ebayListingService.findEbayListing(ebayListing.getId()).getEbayItemId());
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(ebayListing.getLiberecoListing().getId());
        ebayListing.setLiberecoListing(liberecoListing);
        if (updateEbayListing(ebayListing) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private EbayListing updateEbayListing(EbayListing ebayListing) {
        String ebayAuthorizationToken = getMarketplaceToken(ebayListing);
        ebayClient.reviseListing(ebayListing, ebayAuthorizationToken);
        ebayListingService.updateEbayListing(ebayListing);
        return ebayListing;
    }

    private String getMarketplaceToken(EbayListing ebayListing) {
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.EBAY.getName());
        if (marketplace == null) {
            throw new GenericLiberecoException("No marketplace found for marketplace ebay");
        }
        LiberecoListing liberecoListing = ebayListing.getLiberecoListing();
        MarketplaceAuthorizations ebayAuthorization = marketplaceAuthorizationsService
                .findMarketplaceAuthorizations(new MarketplaceAuthorizationsCompositeKey(liberecoListing.getUserId(), marketplace.getId()));
        return ebayAuthorization.getToken();
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, ebayListingService.findEbayListing(id));
        return "ebaylistings/update";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EbayListing ebayListing = ebayListingService.findEbayListing(id);
        deleteEbayListing(ebayListing);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/ebaylistings";
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/ebaylistings/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("liberecoListingId") Long liberecoListingId, @PathVariable("id") Long id) {
        EbayListing ebayListing = ebayListingService.findEbayListing(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (ebayListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        deleteEbayListing(ebayListing);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private void deleteEbayListing(EbayListing ebayListing) {
        Marketplace marketplace = marketplaceService.findMarketplaceByName(MarketplaceName.EBAY.getName());
        if (marketplace == null) {
            throw new GenericLiberecoException("No marketplace found for marketplace ebay");
        }
        LiberecoListing liberecoListing = ebayListing.getLiberecoListing();
        MarketplaceAuthorizations ebayAuthorization = marketplaceAuthorizationsService
                .findMarketplaceAuthorizations(new MarketplaceAuthorizationsCompositeKey(liberecoListing.getUserId(), marketplace.getId()));

        ebayClient.delistItem(ebayListing, ebayAuthorization.getToken(), DelistingReason.OTHER_REASON);
        ebayListingService.deleteEbayListing(ebayListing);
        liberecoListing.getMarketplaces().remove(marketplace);
        liberecoListingService.updateLiberecoListing(liberecoListing);
    }

    void populateEditForm(Model uiModel, EbayListing ebayListing) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);

        uiModel.addAttribute("ebayListing", ebayListing);
        uiModel.addAttribute("liberecolistings", liberecoListingService.findAllNotListedListingsForUser(user.getId(), "ebay"));
        uiModel.addAttribute("returnpolicys", Arrays.asList(ReturnPolicy.values()));
        uiModel.addAttribute("listingDurations", Arrays.asList(ListingDuration.values()));
        uiModel.addAttribute("liberecolistingforupdate", Arrays.asList(ebayListing.getLiberecoListing()));
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    @RequestMapping(value = "/ebaylistings/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EbayListing ebayListing : EbayListing.fromJsonArrayToEbayListings(json)) {
            ebayListingService.saveEbayListing(ebayListing);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/ebaylistings/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (EbayListing ebayListing : EbayListing.fromJsonArrayToEbayListings(json)) {
            if (ebayListingService.updateEbayListing(ebayListing) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

}
