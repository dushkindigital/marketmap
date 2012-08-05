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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.EtsyWhenMade;
import com.libereco.core.domain.EtsyWhoMade;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MergedEbayListing;
import com.libereco.core.domain.MergedEtsyListing;
import com.libereco.core.domain.MergedLiberecoListing;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.exceptions.LiberecoServerException;
import com.libereco.core.service.LiberecoPaymentInformationService;
import com.libereco.core.service.LiberecoShippingInformationService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MergedLiberecoListingService;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.etsy.api.EtsyApi;
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
    public MergeLiberecoListingController(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @RequestMapping(value="/listings", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        String loggedInUser = SecurityUtils.getCurrentLoggedInUsername();
        List<Connection<EbayApi>> ebayConnections = connectionRepository.findConnections(EbayApi.class);
        List<Connection<EtsyApi>> etsyConnections = connectionRepository.findConnections(EtsyApi.class);
        if (CollectionUtils.isEmpty(ebayConnections) && CollectionUtils.isEmpty(etsyConnections)) {
            logger.warn("No connection found for user : " + loggedInUser);
            return "listings/error";
        }

        LiberecoListingForm liberecoListingForm = new LiberecoListingForm();

        if (!CollectionUtils.isEmpty(ebayConnections)) {
            liberecoListingForm.setEbayListingForm(new EbayListingForm());
        }
        if (!CollectionUtils.isEmpty(etsyConnections)) {
            liberecoListingForm.setEtsyListingForm(new EtsyListingForm());
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
    public String create(@Valid LiberecoListingForm liberecoListingForm, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoListingForm);
            return "listings/create";
        }
        uiModel.asMap().clear();
        MergedLiberecoListing mergedLiberecoListing = createLiberecoListing(liberecoListingForm);
//        updateLiberecoListingWithPicture(mergedLiberecoListing, httpServletRequest, picture);
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
        return "liberecolistings/show";
    }


    private MergedLiberecoListing createLiberecoListing(LiberecoListingForm liberecoListing) {
        MergedLiberecoListing mergedLiberecoListing = createLiberecoListingCopy(liberecoListing);
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        mergedLiberecoListing.setUserId(user.getId());
        mergedLiberecoListing.setListingState(ListingState.NEW);
        mergedLiberecoListingService.saveLiberecoListing(mergedLiberecoListing);
        return mergedLiberecoListing;
    }

    private MergedLiberecoListing createLiberecoListingCopy(LiberecoListingForm liberecoListing) {
        MergedLiberecoListing mergedLiberecoListing = new MergedLiberecoListing();
        mergedLiberecoListing.setCategory(liberecoListing.getCategory());
        mergedLiberecoListing.setDescription(liberecoListing.getDescription());
        mergedLiberecoListing.setItemLocation(liberecoListing.getItemLocation());
        mergedLiberecoListing.setLiberecoPaymentInformations(liberecoListing.getLiberecoPaymentInformations());
        mergedLiberecoListing.setListingCondition(liberecoListing.getListingCondition());
        mergedLiberecoListing.setListingState(liberecoListing.getListingState());
        mergedLiberecoListing.setName(liberecoListing.getName());
        mergedLiberecoListing.setPrice(liberecoListing.getPrice());
        mergedLiberecoListing.setQuantity(liberecoListing.getQuantity());
        mergedLiberecoListing.setShippingInformations(liberecoListing.getShippingInformations());

        EbayListingForm ebayListingForm = liberecoListing.getEbayListingForm();
        if (ebayListingForm != null) {
            MergedEbayListing mergedEbayListing = new MergedEbayListing();
            mergedEbayListing.setDispatchTimeMax(ebayListingForm.getDispatchTimeMax());
            mergedEbayListing.setListingDuration(ebayListingForm.getListingDuration());
            mergedEbayListing.setLotSize(ebayListingForm.getLotSize());
            mergedEbayListing.setPaypalEmail(mergedEbayListing.getPaypalEmail());
            mergedEbayListing.setReturnPolicy(ebayListingForm.getReturnPolicy());
            mergedEbayListing.setStartPrice(ebayListingForm.getStartPrice());
            mergedLiberecoListing.setMergedEbayListing(mergedEbayListing);
        }
        EtsyListingForm etsyListingForm = liberecoListing.getEtsyListingForm();
        if (etsyListingForm != null) {
            MergedEtsyListing mergedEtsyListing = new MergedEtsyListing();
            mergedEtsyListing.setSupply(etsyListingForm.isSupply());
            mergedEtsyListing.setWhenMade(etsyListingForm.getWhenMade());
            mergedEtsyListing.setWhoMade(etsyListingForm.getWhoMade());
            mergedLiberecoListing.setMergedEtsyListing(mergedEtsyListing);
        }
        return mergedLiberecoListing;
    }

    private void updateLiberecoListingWithPicture(MergedLiberecoListing liberecoListing, HttpServletRequest httpServletRequest, MultipartFile picture) {
        setPicture(liberecoListing, picture);
        if (liberecoListing.getPicture() != null) {
            boolean isCloudEnvironment = environment.acceptsProfiles("cloud");
            if (isCloudEnvironment) {
                liberecoListing.setPictureUrl("http://libereco.cloudfoundry.com/liberecolistings/" + liberecoListing.getId() + "/image/"
                        + picture.getOriginalFilename());
            } else {
                liberecoListing.setPictureUrl("http://localhost:8080/libereco/liberecolistings/" + liberecoListing.getId() + "/image/"
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

    void populateEditForm(Model uiModel, LiberecoListingForm liberecoListingForm) {
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
