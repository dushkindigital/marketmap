package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping(value = "/marketplaces/{marketplace}/authorize", method = RequestMethod.GET)
    public String authorize(@PathVariable("marketplace") String name) {
        String username = SecurityUtils.getUsername();

        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        MarketplaceName marketplaceName = MarketplaceName.fromString(name);
        String response = null;
        switch (marketplaceName) {
        case EBAY:
            SignInDetails signInDetails = ebayAuthorizer.getSignInDetails();
            response = signInDetails.getSignInUrl();
            pendingMarketplaceAuthorizationsRepository.save(new PendingMarketplaceAuthorizations(liberecoUser, marketplace, signInDetails.getToken(),
                    signInDetails.getSecretToken()));
            break;
        }
        return "redirect:" + response;
    }

    @RequestMapping(value = "/marketplaces/{marketplace}/fetchToken", method = RequestMethod.GET)
    public String fetchToken(@PathVariable("marketplace") String name, HttpServletRequest httpServletRequest) {
        String username = SecurityUtils.getUsername();
        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        MarketplaceName marketplaceName = MarketplaceName.fromString(name);
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
            } catch (Exception e) {
                throw new RuntimeException("Not able to fetch token", e);
            }

            break;
        default:
            throw new IllegalArgumentException("Illegal Marktplace : " + marketplaceName);
        }
        return "redirect:/" + username + "/marketplaceauthorizations/" + name;
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations/{name}", produces = "text/html")
    public String showMarketplaceAuthorizations(@PathVariable("username") String username, @PathVariable("name") String name, Model uiModel) {
        LiberecoUser liberecoUser = fetchUser(username);
        Marketplace marketplace = fetchMarketplace(name);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("marketplaceauthorizations", marketplaceAuthorizationsService
                .findMarketplaceAuthorizations(new MarketplaceAuthorizationsCompositeKey(liberecoUser.getId(), marketplace.getId())));
        uiModel.addAttribute("username",username);
        return "marketplace/authorizations/show";
    }

    @RequestMapping(value = "/{username}/marketplaceauthorizations", produces = "text/html")
    public String listMarketplaceAuthorizations(@PathVariable("username") String username, Model uiModel) {
        LiberecoUser liberecoUser = fetchUser(username);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("allMarketplaceauthorizations", marketplaceAuthorizationsService.findAllMarketplaceAuthorizationsForUser(liberecoUser.getId()));
        return "marketplace/authorizations/list";
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
