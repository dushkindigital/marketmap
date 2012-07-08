package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.domain.PendingMarketplaceAuthorizations;
import com.libereco.core.domain.UserMarketplaceKey;
import com.libereco.core.repository.PendingMarketplaceAuthorizationsRepository;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.web.auth.SignInDetails;
import com.libereco.web.auth.ebay.EbayAuthorizer;
import com.libereco.web.auth.ebay.EbayToken;
import com.libereco.web.common.MarketplaceName;
import com.libereco.web.security.SecurityUtils;

@Controller
public class MarketPlaceAuthorizationController {

    @Autowired
    LiberecoUserService liberecoUserService;

    @Autowired
    MarketplaceService marketplaceService;

    @Autowired
    EbayAuthorizer ebayAuthorizer;

    @Autowired
    PendingMarketplaceAuthorizationsRepository pendingMarketplaceAuthorizationsRepository;

    @Autowired
    MarketplaceAuthorizationsService marketplaceAuthorizationsService;

    /**
     * This controller method will be invoked by user using the libereco
     * application to initiate the authorization mechanism. This method will be
     * called for all the marketplaces. This api assumes that user is
     * authenticated with Libereco application.<br>
     * <br>
     * 
     * TODO : I still think that this url is not entirely restful in nature as I
     * am giving the action "authorize" in url. Will spend some time on it
     * later.
     * 
     * @param username
     * @param name
     * @return
     */
    @RequestMapping(value = "/marketplaces/{marketplace}/authorize", method = RequestMethod.GET,produces = "text/html")
    public String authorize(@PathVariable("marketplace") String name) {
        String redirectUrl = getRedirectUrl(name);
        return "redirect:" + redirectUrl;
    }

    @RequestMapping(value = "/marketplaces/{marketplace}/authorize", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> authorizeJson(@PathVariable("marketplace") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        String redirectUrl = getRedirectUrl(name);
        if (StringUtils.isBlank(redirectUrl)) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(redirectUrl, headers, HttpStatus.OK);
    }

    private String getRedirectUrl(String name) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        MarketplaceName marketplaceName = MarketplaceName.fromString(name);
        String redirectUrl = null;
        switch (marketplaceName) {
        case EBAY:
            SignInDetails signInDetails = ebayAuthorizer.getSignInDetails();
            redirectUrl = signInDetails.getSignInUrl();
            pendingMarketplaceAuthorizationsRepository.save(new PendingMarketplaceAuthorizations(liberecoUser, marketplace, signInDetails.getToken(),
                    signInDetails.getSecretToken()));
            break;
        }
        return redirectUrl;
    }

    @RequestMapping(value = "/marketplaces/{marketplace}/fetchToken", method = RequestMethod.GET, produces = "text/html")
    public String fetchToken(@PathVariable("marketplace") String name) {
        String username = SecurityUtils.getCurrentLoggedInUsername();
        persistMarketplaceAuthorizationToken(name, username);
        return "redirect:/" + username + "/marketplaceauthorizations/" + name;
    }

    @RequestMapping(value = "/marketplaces/{marketplace}/fetchToken", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> fetchTokenJson(@PathVariable("marketplace") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        String username = SecurityUtils.getCurrentLoggedInUsername();
        String token = persistMarketplaceAuthorizationToken(name, username);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(token, headers, HttpStatus.CREATED);
    }

    private String persistMarketplaceAuthorizationToken(String name, String username) {
        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        MarketplaceName marketplaceName = MarketplaceName.fromString(name);
        String token = null;
        switch (marketplaceName) {
        case EBAY:
            EbayToken ebayToken = null;
            try {
                PendingMarketplaceAuthorizations pendingMarketplaceAuthorization = pendingMarketplaceAuthorizationsRepository
                        .findOne(new UserMarketplaceKey(liberecoUser.getId(), marketplace.getId()));
                String sessionId = pendingMarketplaceAuthorization.getRequestToken();
                ebayToken = ebayAuthorizer.fetchToken(sessionId);
                MarketplaceAuthorizations marketplaceAuthorization = createNewMarketplaceAuthorization(liberecoUser, marketplace, ebayToken);
                marketplaceAuthorizationsService.saveMarketplaceAuthorizations(marketplaceAuthorization);
                pendingMarketplaceAuthorizationsRepository.delete(pendingMarketplaceAuthorization);
                token = ebayToken.getToken();
            } catch (Exception e) {
                throw new RuntimeException("Not able to fetch token", e);
            }

            break;
        default:
            throw new IllegalArgumentException("Illegal Marktplace : " + marketplaceName);
        }
        return token;
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations/{name}", produces = "text/html")
    public String showMarketplaceAuthorizations(@PathVariable("username") String username, @PathVariable("name") String name, Model uiModel) {
        MarketplaceAuthorizations marketplaceAuthorization = getMarketplaceAuthorization(username, name);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("marketplaceauthorizations", marketplaceAuthorization);
        uiModel.addAttribute("username", username);
        return "marketplace/authorizations/show";
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations/{name}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showMarketplaceAuthorizationsJson(@PathVariable("username") String username, @PathVariable("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        MarketplaceAuthorizations marketplaceAuthorization = getMarketplaceAuthorization(username, name);
        if (marketplaceAuthorization == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(marketplaceAuthorization.toJson(), headers, HttpStatus.OK);
    }

    private MarketplaceAuthorizations getMarketplaceAuthorization(String username, String name) {
        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        MarketplaceAuthorizations marketplaceAuthorization = marketplaceAuthorizationsService
                .findMarketplaceAuthorizations(new MarketplaceAuthorizationsCompositeKey(liberecoUser.getId(), marketplace.getId()));
        return marketplaceAuthorization;
    }

    private Marketplace fetchMarketplace(String name) {
        Marketplace marketplace = marketplaceService.findMarketplaceByName(name);
        if (marketplace == null) {
            throw new RuntimeException("Marketplace does not exist for given marketplace : " + marketplace);
        }
        return marketplace;
    }

    private LiberecoUser fetchUser(String username) {
        LiberecoUser liberecoUser = liberecoUserService.findUserByUsername(username);
        if (liberecoUser == null) {
            throw new RuntimeException("User does not exist for given username : " + username);
        }
        return liberecoUser;
    }

    private MarketplaceAuthorizations createNewMarketplaceAuthorization(LiberecoUser liberecoUser, Marketplace marketplace, EbayToken ebayToken) {
        MarketplaceAuthorizations marketplaceAuthorization = new MarketplaceAuthorizations();
        marketplaceAuthorization.setExpirationTime(ebayToken.getExpirationTime().getTime());
        marketplaceAuthorization.setLiberecoUser(liberecoUser);
        marketplaceAuthorization.setToken(ebayToken.getToken());
        marketplaceAuthorization.setTokenSecret(null);
        marketplaceAuthorization.setLiberecoUser(liberecoUser);
        marketplaceAuthorization.setKey(new MarketplaceAuthorizationsCompositeKey(liberecoUser.getId(), marketplace.getId()));
        return marketplaceAuthorization;
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations", produces = "text/html")
    public String listMarketplaceAuthorizations(@PathVariable("username") String username, Model uiModel) {
        LiberecoUser liberecoUser = fetchUser(username);
        List<MarketplaceAuthorizations> allMarketplaceAuthorizations = marketplaceAuthorizationsService
                .findAllMarketplaceAuthorizationsForUser(liberecoUser.getId());

        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("allMarketplaceauthorizations",
                allMarketplaceAuthorizations);
        return "marketplace/authorizations/list";
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listMarketplaceAuthorizationsJson(@PathVariable("username") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        LiberecoUser liberecoUser = fetchUser(username);
        List<MarketplaceAuthorizations> allMarketplaceAuthorizations = marketplaceAuthorizationsService
                .findAllMarketplaceAuthorizationsForUser(liberecoUser.getId());
        return new ResponseEntity<String>(MarketplaceAuthorizations.toJsonArray(allMarketplaceAuthorizations), headers, HttpStatus.OK);
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("marketplaceAuthorizations_expirationtime_date_format",
                DateTimeFormat.patternForStyle("LL", LocaleContextHolder.getLocale()));
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
