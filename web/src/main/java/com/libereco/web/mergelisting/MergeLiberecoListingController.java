package com.libereco.web.mergelisting;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
import com.libereco.core.exceptions.GenericLiberecoException;
import com.libereco.core.exceptions.LiberecoServerException;
import com.libereco.core.service.LiberecoPaymentInformationService;
import com.libereco.core.service.LiberecoShippingInformationService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MergedLiberecoListingService;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.EtsyListing;
import com.libereco.springsocial.etsy.api.ListingBuilder;
import com.libereco.springsocial.etsy.api.impl.EtsyByteArrayResource;
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

        MergedLiberecoListing mergedLiberecoListing = new MergedLiberecoListing();

        if (setupProviderSpecificForms(mergedLiberecoListing) == null) {
            return "listings/error";
        }
        populateEditForm(uiModel, mergedLiberecoListing);

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

    private MergedLiberecoListing setupProviderSpecificForms(MergedLiberecoListing mergedLiberecoListing) {
        String loggedInUser = SecurityUtils.getCurrentLoggedInUsername();
        List<Connection<EbayApi>> ebayConnections = connectionRepository.findConnections(EbayApi.class);
        List<Connection<EtsyApi>> etsyConnections = connectionRepository.findConnections(EtsyApi.class);
        if (CollectionUtils.isEmpty(ebayConnections) && CollectionUtils.isEmpty(etsyConnections)) {
            logger.warn("No connection found for user : " + loggedInUser);
            return null;
        }

        if (!CollectionUtils.isEmpty(ebayConnections) && mergedLiberecoListing.getMergedEbayListing() == null) {
            mergedLiberecoListing.setMergedEbayListing(new MergedEbayListing());
        }
        if (!CollectionUtils.isEmpty(etsyConnections)) {
            mergedLiberecoListing.setMergedEtsyListing(new MergedEtsyListing());
        }
        return mergedLiberecoListing;
    }

    @RequestMapping(value = "/listings", method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid MergedLiberecoListing mergedLiberecoListing, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest, @RequestParam MultipartFile picture) {
        if (bindingResult.hasErrors()) {
            if (setupProviderSpecificForms(mergedLiberecoListing) == null) {
                return "listing/error";
            }
            populateEditForm(uiModel, mergedLiberecoListing);
            return "listings/create";
        }
        uiModel.asMap().clear();
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        mergedLiberecoListing.setUserId(user.getId());
        mergedLiberecoListing.setListingState(ListingState.NEW);

        ItemType listing = toEbayItemType(mergedLiberecoListing);
        logger.info("Creating Ebay Listing.....");
        ItemType createdEbayListing = ebayApi.listingOperations().createListing(listing, null);
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
        updateLiberecoListingWithPicture(mergedLiberecoListing, httpServletRequest, picture);

        String[] pictureUrls = { mergedLiberecoListing.getPictureUrl() };

        ebayApi.listingOperations().updateEbayListing(createdEbayListing, pictureUrls);

        etsyApi.listingOperations().uploadListingImage(createdEtsyListing.getListingId(),
                new EtsyByteArrayResource(mergedLiberecoListing.getPicture(), mergedLiberecoListing.getPictureName()));
        return "redirect:/listings/" + encodeUrlPathSegment(mergedLiberecoListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/listings/{id}/image/{pictureName}", method = RequestMethod.GET, produces = "text/html")
    public void getImage(@PathVariable("id") Long id, @PathVariable("pictureName") String pictureName, HttpServletRequest req,
            HttpServletResponse res) {
        MergedLiberecoListing liberecoListing = mergedLiberecoListingService.findLiberecoListing(id);
        if (liberecoListing == null) {
            throw new GenericLiberecoException("No libereco listing found for id " + id);
        }

        res.setHeader("Cache-Control", "no-store");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);
        res.setContentType("image/jpg");
        try {
            ServletOutputStream ostream = res.getOutputStream();
            IOUtils.write(liberecoListing.getPicture(), ostream);
            ostream.flush();
            ostream.close();
        } catch (Exception e) {
            throw new LiberecoServerException("Not able to read image from the server.", e);
        }
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
        MergedLiberecoListing mergedLiberecoListing = mergedLiberecoListingService.findLiberecoListing(id);
        if (setupProviderSpecificForms(mergedLiberecoListing) == null) {
            return "listing/error";
        }
        populateEditForm(uiModel, mergedLiberecoListing);
        return "listings/update";
    }

    @RequestMapping(value = "/listings/update/{id}", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid MergedLiberecoListing mergedLiberecoListing, @PathVariable("id") Long id, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, mergedLiberecoListing);
            return "listings/update";
        }
        uiModel.asMap().clear();

        MergedLiberecoListing persistedMergedLiberecoListing = mergedLiberecoListingService.findLiberecoListing(id);
       
        mergedLiberecoListing.setId(id);
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        mergedLiberecoListing.setUserId(user.getId());
        mergedLiberecoListing.setListingState(ListingState.NEW);

        ItemType listing = toEbayItemType(mergedLiberecoListing);
        if (mergedLiberecoListing.getMergedEbayListing() != null) {
            listing.setItemID(persistedMergedLiberecoListing.getMergedEbayListing().getEbayItemId());
        }
        String[] pictureUrls = { mergedLiberecoListing.getPictureUrl() };
        logger.info("Updating Ebay Listing.....");
        
        ItemType updatedEbayListing = ebayApi.listingOperations().updateEbayListing(listing, pictureUrls);
        logger.info("Updated Ebay Listing with id : " + updatedEbayListing.getItemID());

        logger.info("Updating Etsy Listing ....");
        EtsyListing etsyListing = toEtsyListing(mergedLiberecoListing);
        if (mergedLiberecoListing.getMergedEtsyListing() != null) {
            etsyListing.setListingId(persistedMergedLiberecoListing.getMergedEtsyListing().getListingId());
        }
        EtsyListing updatedEtsyListing = etsyApi.listingOperations().updateListing(etsyListing);
        logger.info("Updated Etsy Listing with id : " + updatedEtsyListing.getListingId());

        mergedLiberecoListing.getMergedEbayListing().setEbayItemId(updatedEbayListing.getItemID());
        String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
        ebayItemUrl += updatedEbayListing.getItemID();
        mergedLiberecoListing.getMergedEbayListing().setEbayItemUrl(ebayItemUrl);

        mergedLiberecoListing.getMergedEtsyListing().setListingId(updatedEtsyListing.getListingId());
        mergedLiberecoListing.getMergedEtsyListing().setEtsyListingUrl(updatedEtsyListing.getUrl());

        mergedLiberecoListingService.updateLiberecoListing(mergedLiberecoListing);
        return "redirect:/listings/" + encodeUrlPathSegment(mergedLiberecoListing.getId().toString(), httpServletRequest);
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

    private void updateLiberecoListingWithPicture(MergedLiberecoListing liberecoListing, HttpServletRequest httpServletRequest, MultipartFile picture) {
        setPicture(liberecoListing, picture);
        if (liberecoListing.getPicture() != null) {
            boolean isCloudEnvironment = environment.acceptsProfiles("cloud");
            if (isCloudEnvironment) {
                liberecoListing.setPictureUrl("http://libereco.cloudfoundry.com/listings/" + liberecoListing.getId() + "/image/"
                        + picture.getOriginalFilename());
            } else {
                liberecoListing.setPictureUrl("http://localhost:8443/listings/" + liberecoListing.getId() + "/image/"
                        + picture.getOriginalFilename());
            }

        }
        mergedLiberecoListingService.updateLiberecoListing(liberecoListing);
    }

    private void setPicture(MergedLiberecoListing liberecoListing, MultipartFile picture) {
        try {
            logger.info("Image updloaded name : " + picture.getOriginalFilename() + " , and its size " + picture.getSize());
            liberecoListing.setPicture(picture.getBytes());
            liberecoListing.setPictureName(picture.getOriginalFilename());
        } catch (Exception e) {
            throw new LiberecoServerException("Not able to upload the picture at this moment.", e);
        }
    }

}
