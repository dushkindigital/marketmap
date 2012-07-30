package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.DelistingReason;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.exceptions.GenericLiberecoException;
import com.libereco.core.exceptions.LiberecoResourceNotFoundException;
import com.libereco.core.exceptions.LiberecoServerException;
import com.libereco.core.service.EbayListingService;
import com.libereco.core.service.LiberecoListingService;
import com.libereco.core.service.LiberecoPaymentInformationService;
import com.libereco.core.service.LiberecoShippingInformationService;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.web.common.MarketplaceName;
import com.libereco.web.external.ebay.EbayClient;
import com.libereco.web.security.SecurityUtils;

@Controller
public class LiberecoListingController {

    @Autowired
    LiberecoListingService liberecoListingService;
    @Autowired
    LiberecoUserService liberecoUserService;
    @Autowired
    LiberecoShippingInformationService liberecoShippingInformationService;
    @Autowired
    LiberecoPaymentInformationService liberecoPaymentInformationService;
    @Autowired
    EbayListingService ebayListingService;
    @Autowired
    EbayClient ebayClient;
    @Autowired
    MarketplaceService marketplaceService;
    @Autowired
    MarketplaceAuthorizationsService marketplaceAuthorizationsService;
    @Autowired
    Environment environment;
    

    private Logger logger = Logger.getLogger(LiberecoListingController.class);

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @RequestMapping(value = "/liberecolistings", method = RequestMethod.POST, produces = "text/html")
    @Secured(value = { "ROLE_USER" })
    public String create(@Valid LiberecoListing liberecoListing, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest,
            @RequestParam MultipartFile picture) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoListing);
            return "liberecolistings/create";
        }
        uiModel.asMap().clear();
        createLiberecoListing(liberecoListing);
        updateLiberecoListingWithPicture(liberecoListing, httpServletRequest, picture);
        return "redirect:/liberecolistings/" + encodeUrlPathSegment(liberecoListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/liberecolistings", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> createLiberecoListingFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        LiberecoListing liberecoListing = LiberecoListing.fromJsonToLiberecoListing(json);
        createLiberecoListing(liberecoListing);
        return new ResponseEntity<String>(liberecoListing.toJson(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/liberecolistings/{liberecoListingId}/image", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> uploadImageForLiberecoListingFromJson(@PathVariable("liberecoListingId") Long liberecoListingId,
            HttpServletRequest httpServletRequest, @RequestParam MultipartFile picture) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(liberecoListingId);
        if (liberecoListing == null) {
            throw new LiberecoResourceNotFoundException("No liberecolisting found for id " + liberecoListingId);
        }
        updateLiberecoListingWithPicture(liberecoListing, httpServletRequest, picture);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private void createLiberecoListing(LiberecoListing liberecoListing) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        liberecoListing.setUserId(user.getId());
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListingService.saveLiberecoListing(liberecoListing);
    }

    private void updateLiberecoListingWithPicture(LiberecoListing liberecoListing, HttpServletRequest httpServletRequest, MultipartFile picture) {
        setPicture(liberecoListing, picture);
        if (liberecoListing.getPicture() != null) {
            boolean isCloudEnvironment = environment.acceptsProfiles("cloud");
            if(isCloudEnvironment){
                liberecoListing.setPictureUrl("http://libereco.cloudfoundry.com/liberecolistings/" + liberecoListing.getId() + "/image/"
                        + picture.getOriginalFilename());
            }else{
                liberecoListing.setPictureUrl("http://localhost:8080/libereco/liberecolistings/" + liberecoListing.getId() + "/image/"
                        + picture.getOriginalFilename());
            }
            
        }
        liberecoListingService.updateLiberecoListing(liberecoListing);
    }

    private void setPicture(LiberecoListing liberecoListing, MultipartFile picture) {
        try {
            logger.info("Image updloaded name : " + picture.getOriginalFilename() + " , and its size " + picture.getSize());
            liberecoListing.setPicture(picture.getBytes());
            liberecoListing.setPictureName(picture.getOriginalFilename());
        } catch (Exception e) {
            throw new LiberecoServerException("Not able to upload the picture at this moment.", e);
        }
    }

    @RequestMapping(value = "/liberecolistings/{id}/image/{pictureName}", method = RequestMethod.GET, produces = "text/html")
    public void getImage(@PathVariable("id") Long id, @PathVariable("pictureName") String pictureName, HttpServletRequest req,
            HttpServletResponse res) {
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(id);
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

    @RequestMapping(value = "/liberecolistings", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        LiberecoListing liberecoListing = new LiberecoListing();
        populateEditForm(uiModel, liberecoListing);
        List<String[]> dependencies = new ArrayList<String[]>();
        if (liberecoShippingInformationService.countAllShippingInformations() == 0) {
            dependencies.add(new String[] { "shippinginformation", "shippinginformations" });
        }
        if (liberecoPaymentInformationService.countAllLiberecoPaymentInformations() == 0) {
            dependencies.add(new String[] { "paymentinformation", "paymentinformations" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "liberecolistings/create";
    }

    @RequestMapping(value = "/liberecolistings/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(id);
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

    @RequestMapping(value = "/liberecolistings/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (liberecoListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(liberecoListing.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings", produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("liberecolistings", liberecoListingService.findLiberecoListingEntries(user.getId(), firstResult, sizeNo));
            float nrOfPages = (float) liberecoListingService.countAllLiberecoListings() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("liberecolistings", liberecoListingService.findAllLiberecoListings(user.getId()));
        }
        addDateTimeFormatPatterns(uiModel);
        return "liberecolistings/list";
    }

    @RequestMapping(value = "/liberecolistings", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(username);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<LiberecoListing> result = liberecoListingService.findAllLiberecoListings(user.getId());
        return new ResponseEntity<String>(LiberecoListing.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/liberecolistings/update", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid LiberecoListing liberecoListing, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest,
            @RequestParam MultipartFile picture) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoListing);
            return "liberecolistings/update";
        }
        uiModel.asMap().clear();
        LiberecoListing updatedLiberecoListing = updateLiberecoListing(liberecoListing);
        updateLiberecoListingWithPicture(updatedLiberecoListing, httpServletRequest, picture);
        liberecoListingService.updateLiberecoListing(updatedLiberecoListing);
        return "redirect:/liberecolistings/" + encodeUrlPathSegment(updatedLiberecoListing.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/liberecolistings/update", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        LiberecoListing liberecoListing = LiberecoListing.fromJsonToLiberecoListing(json);
        if (updateLiberecoListing(liberecoListing) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private LiberecoListing updateLiberecoListing(LiberecoListing updatedLiberecoListing) {
        LiberecoListing liberecoListing =
                liberecoListingService.findLiberecoListing(updatedLiberecoListing.getId());
        updateAllMarketplaceListings(liberecoListing);
        copyProperties(liberecoListing, updatedLiberecoListing);
        liberecoListingService.updateLiberecoListing(liberecoListing);
        return liberecoListing;
    }

    private void copyProperties(LiberecoListing persistedLiberecoListing, LiberecoListing updatedLiberecoListing) {
        persistedLiberecoListing.setName(updatedLiberecoListing.getName());
        persistedLiberecoListing.setDescription(updatedLiberecoListing.getDescription());
        persistedLiberecoListing.setItemLocation(updatedLiberecoListing.getItemLocation());
        persistedLiberecoListing.setLiberecoPaymentInformations(updatedLiberecoListing.getLiberecoPaymentInformations());
        persistedLiberecoListing.setCategory(updatedLiberecoListing.getCategory());
        persistedLiberecoListing.setListingCondition(updatedLiberecoListing.getListingCondition());
        persistedLiberecoListing.setListingState(updatedLiberecoListing.getListingState());
        persistedLiberecoListing.setPrice(updatedLiberecoListing.getPrice());
        persistedLiberecoListing.setQuantity(updatedLiberecoListing.getQuantity());
    }

    private void updateAllMarketplaceListings(LiberecoListing liberecoListing) {
        EbayListing ebayListing = ebayListingService.findEbayListing(liberecoListing);
        if (ebayListing != null) {
            ebayListing.setLiberecoListing(liberecoListing);
            String ebayAuthorizationToken = getMarketplaceToken(ebayListing);
            ebayClient.reviseListing(ebayListing, ebayAuthorizationToken);
            ebayListingService.updateEbayListing(ebayListing);
        }
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

    @RequestMapping(value = "/liberecolistings/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, liberecoListingService.findLiberecoListing(id));
        return "liberecolistings/update";
    }

    @RequestMapping(value = "/liberecolistings/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(id);
        deleteLiberecoListing(liberecoListing);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/liberecolistings";
    }

    @RequestMapping(value = "/liberecolistings/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        LiberecoListing liberecoListing = liberecoListingService.findLiberecoListing(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (liberecoListing == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        deleteLiberecoListing(liberecoListing);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private void deleteLiberecoListing(LiberecoListing liberecoListing) {
        EbayListing ebayListing = ebayListingService.findEbayListing(liberecoListing);
        if (ebayListing != null) {
            String ebayAuthorizationToken = getMarketplaceToken(ebayListing);
            ebayClient.delistItem(ebayListing, ebayAuthorizationToken, DelistingReason.OTHER_REASON);
        }
        liberecoListingService.deleteLiberecoListing(liberecoListing);
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("liberecoListing_listingduration_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, LiberecoListing liberecoListing) {
        uiModel.addAttribute("liberecoListing", liberecoListing);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("liberecocategorys", Arrays.asList(LiberecoCategory.values()));
        uiModel.addAttribute("listingconditions", ListingCondition.messages());
        uiModel.addAttribute("listingstates", Arrays.asList(ListingState.values()));
        uiModel.addAttribute("shippinginformations", liberecoShippingInformationService.findAllShippingInformations());
        uiModel.addAttribute("paymentinformations", liberecoPaymentInformationService.findAllLiberecoPaymentInformations());
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

    @RequestMapping(value = "/liberecolistings/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (LiberecoListing liberecoListing : LiberecoListing.fromJsonArrayToLiberecoListings(json)) {
            liberecoListingService.saveLiberecoListing(liberecoListing);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/liberecolistings/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (LiberecoListing liberecoListing : LiberecoListing.fromJsonArrayToLiberecoListings(json)) {
            if (liberecoListingService.updateLiberecoListing(liberecoListing) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

}
