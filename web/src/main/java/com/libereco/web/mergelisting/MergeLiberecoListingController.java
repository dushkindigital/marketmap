package com.libereco.web.mergelisting;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.ebay.soap.eBLBaseComponents.ItemType;
import com.libereco.core.domain.EtsyWhenMade;
import com.libereco.core.domain.EtsyWhoMade;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MergedEbayListing;
import com.libereco.core.domain.MergedEtsyListing;
import com.libereco.core.domain.MergedLiberecoListing;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.service.LiberecoPaymentInformationService;
import com.libereco.core.service.LiberecoShippingInformationService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MergedLiberecoListingService;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.EtsyListing;
import com.libereco.springsocial.etsy.api.ListingBuilder;
import com.libereco.web.security.SecurityUtils;

@Controller
public class MergeLiberecoListingController {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final ConnectionRepository connectionRepository;

    @Inject
    LiberecoShippingInformationService liberecoShippingInformationService;
    @Inject
    LiberecoPaymentInformationService liberecoPaymentInformationService;
    @Inject
    LiberecoUserService liberecoUserService;
    @Inject
    Environment environment;
    @Inject
    MergedLiberecoListingService mergedLiberecoListingService;
    @Inject
    EtsyApi etsyApi;
    @Inject
    EbayApi ebayApi;

    @Inject
    public MergeLiberecoListingController(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @RequestMapping(value = "/listings", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        String loggedInUser = SecurityUtils.getCurrentLoggedInUsername();
        List<Connection<EbayApi>> ebayConnections = connectionRepository.findConnections(EbayApi.class);
        List<Connection<EtsyApi>> etsyConnections = connectionRepository.findConnections(EtsyApi.class);
        if (CollectionUtils.isEmpty(ebayConnections) && CollectionUtils.isEmpty(etsyConnections)) {
            logger.warn("No connection found for user : " + loggedInUser);
            return "listings/error";
        }

        MergedLiberecoListing liberecoListingForm = new MergedLiberecoListing();

        if (!CollectionUtils.isEmpty(ebayConnections)) {
            liberecoListingForm.setMergedEbayListing(new MergedEbayListing());
        }
        if (!CollectionUtils.isEmpty(etsyConnections)) {
            liberecoListingForm.setMergedEtsyListing(new MergedEtsyListing());
        }

        populateEditForm(uiModel, liberecoListingForm);
        List<String[]> dependencies = new ArrayList<String[]>();
        if (liberecoShippingInformationService.countAllShippingInformations() == 0) {
            dependencies.add(new String[] { "shippinginformation", "shippinginformations" });
        }
        if (liberecoPaymentInformationService.countAllLiberecoPaymentInformations() == 0) {
            dependencies.add(new String[] { "paymentinformation", "paymentinformations" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "listings/create";
    }

    @RequestMapping(value = "/listings", method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid MergedLiberecoListing liberecoListingForm, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoListingForm);
            return "listings/create";
        }
        uiModel.asMap().clear();
        MergedLiberecoListing mergedLiberecoListing = persistMergedLiberecoListing(liberecoListingForm);
        // updateLiberecoListingWithPicture(mergedLiberecoListing,
        // httpServletRequest, picture);
        return "redirect:/listings/" + encodeUrlPathSegment(mergedLiberecoListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/listings/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        MergedLiberecoListing liberecoListing = mergedLiberecoListingService.findLiberecoListing(id);
        uiModel.addAttribute("liberecolisting", liberecoListing);
        uiModel.addAttribute("itemId", id);
        if (!CollectionUtils.isEmpty(liberecoListing.getMarketplaces())) {
            StringBuilder sb = new StringBuilder();
            for (Marketplace marketplace : liberecoListing.getMarketplaces()) {
                sb.append(marketplace.getMarketplaceName()).append(" , ");
            }
            String marketplaces = StringUtils.removeEnd(sb.toString(), " , ");
            uiModel.addAttribute("marketplaces", marketplaces);
        }
        return "listings/show";
    }

    @RequestMapping(value = "/listings/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, mergedLiberecoListingService.findLiberecoListing(id));
        return "listings/update";
    }

    @RequestMapping(value = "/listings/update/{id}", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid MergedLiberecoListing liberecoListingForm, @PathVariable("id") Long id, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoListingForm);
            return "listings/update";
        }
        uiModel.asMap().clear();

        return null;
    }

    private MergedLiberecoListing updateMergedLiberecoListing(Long id, MergedLiberecoListing mergedLiberecoListing) {
        mergedLiberecoListing.setId(id);
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        mergedLiberecoListing.setUserId(user.getId());
        mergedLiberecoListing.setListingState(ListingState.NEW);

        ItemType listing = toEbayItemType(mergedLiberecoListing);
        String[] pictureUrls = {};
        logger.info("Updating Ebay Listing.....");
        ItemType updatedEbayListing = ebayApi.listingOperations().updateEbayListing(listing, pictureUrls);
        logger.info("Updated Ebay Listing with id : " + updatedEbayListing.getItemID());

        logger.info("Updating Etsy Listing ....");
        EtsyListing updatedEtsyListing = etsyApi.listingOperations().updateListing(toEtsyListing(mergedLiberecoListing));
        logger.info("Updated Etsy Listing with id : " + updatedEtsyListing.getListingId());

        mergedLiberecoListing.getMergedEbayListing().setEbayItemId(updatedEbayListing.getItemID());
        String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
        ebayItemUrl += updatedEbayListing.getItemID();
        mergedLiberecoListing.getMergedEbayListing().setEbayItemUrl(ebayItemUrl);

        mergedLiberecoListing.getMergedEtsyListing().setListingId(updatedEtsyListing.getListingId());
        mergedLiberecoListing.getMergedEtsyListing().setEtsyListingUrl(updatedEtsyListing.getUrl());

        mergedLiberecoListingService.updateLiberecoListing(mergedLiberecoListing);
        return mergedLiberecoListing;
    }

    private MergedLiberecoListing persistMergedLiberecoListing(MergedLiberecoListing mergedLiberecoListing) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        mergedLiberecoListing.setUserId(user.getId());
        mergedLiberecoListing.setListingState(ListingState.NEW);

        ItemType listing = toEbayItemType(mergedLiberecoListing);
        String[] pictureUrls = {};
        logger.info("Creating Ebay Listing.....");
        ItemType createdEbayListing = ebayApi.listingOperations().createEbayListing(listing, pictureUrls);
        logger.info("Created Ebay Listing with id : " + createdEbayListing.getItemID());

        logger.info("Creating Etsy Listing ....");
        EtsyListing createdEtsyListing = etsyApi.listingOperations().createListing(toEtsyListing(mergedLiberecoListing));
        logger.info("Created Etsy Listing with id : " + createdEtsyListing.getListingId());

        mergedLiberecoListing.getMergedEbayListing().setEbayItemId(createdEbayListing.getItemID());
        String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
        ebayItemUrl += createdEbayListing.getItemID();
        mergedLiberecoListing.getMergedEbayListing().setEbayItemUrl(ebayItemUrl);

        mergedLiberecoListing.getMergedEtsyListing().setListingId(createdEtsyListing.getListingId());
        mergedLiberecoListing.getMergedEtsyListing().setEtsyListingUrl(createdEtsyListing.getUrl());

        mergedLiberecoListingService.saveLiberecoListing(mergedLiberecoListing);
        return mergedLiberecoListing;
    }

    private com.libereco.springsocial.etsy.api.EtsyListing toEtsyListing(MergedLiberecoListing liberecoListing) {
        MergedEtsyListing etsyListing = liberecoListing.getMergedEtsyListing();
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

    private ItemType toEbayItemType(MergedLiberecoListing mergedLiberecoListing) {
        return EbayListingConverter.toItemType(mergedLiberecoListing);
    }

    void populateEditForm(Model uiModel, MergedLiberecoListing liberecoListingForm) {
        uiModel.addAttribute("liberecoListingForm", liberecoListingForm);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("liberecocategorys", Arrays.asList(LiberecoCategory.values()));
        uiModel.addAttribute("listingconditions", ListingCondition.messages());
        uiModel.addAttribute("listingstates", Arrays.asList(ListingState.values()));
        uiModel.addAttribute("returnpolicys", Arrays.asList(ReturnPolicy.values()));
        uiModel.addAttribute("listingDurations", Arrays.asList(ListingDuration.values()));
        uiModel.addAttribute("whomade", Arrays.asList(EtsyWhoMade.values()));
        uiModel.addAttribute("whenmade", Arrays.asList(EtsyWhenMade.values()));
        uiModel.addAttribute("shippinginformations", liberecoShippingInformationService.findAllShippingInformations());
        uiModel.addAttribute("paymentinformations", liberecoPaymentInformationService.findAllLiberecoPaymentInformations());
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("liberecoListing_listingduration_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
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

}
