package com.libereco.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    @RequestMapping(value = "/{username}/authorize/{marketplace}", method = RequestMethod.GET)
    public String authorize(@PathVariable("username") String username, @PathVariable("marketplace") String name) {
        LiberecoUser liberecoUser = liberecoUserService.findUserByUsername(username);
        if (liberecoUser == null) {
            throw new RuntimeException("User does not exist for given username : " + username);
        }
        Marketplace marketplace = marketplaceService.findMarketplaceByName(name);
        if (marketplace == null) {
            throw new RuntimeException("Marketplace does not exist for given marketplace : " + marketplace);
        }
        MarketplaceName marketplaceName = MarketplaceName.valueOf(name);
        String response = null;
        switch (marketplaceName) {
        case EBAY:
            SignInDetails signInDetails = ebayAuthorizer.getSignInDetails();
            response = signInDetails.getSignInUrl();
            pendingMarketplaceAuthorizationsRepository.save(new PendingMarketplaceAuthorizations(liberecoUser, marketplace, signInDetails.getToken(),
                    signInDetails.getSecretToken()));
            break;
        default:
            throw new IllegalArgumentException("Illegal Marktplace : " + marketplaceName);
        }
        return "redirect:" + response;
    }

    @RequestMapping(value = "/{username}/fetchToken/{marketplace}", method = RequestMethod.GET)
    public String fetchToken(@PathVariable("username") String username, @PathVariable("marketplace") String name) {
        System.out.println("In fetch request.....");
        LiberecoUser liberecoUser = liberecoUserService.findUserByUsername(username);
        if (liberecoUser == null) {
            throw new RuntimeException("User does not exist for given username : " + username);
        }
        Marketplace marketplace = marketplaceService.findMarketplaceByName(name);
        if (marketplace == null) {
            throw new RuntimeException("Marketplace does not exist for given marketplace : " + marketplace);
        }
        MarketplaceName marketplaceName = MarketplaceName.valueOf(name);
        String response = null;
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
            } catch (Exception e) {
                throw new RuntimeException("Not able to fetch token", e);
            }

            break;
        default:
            throw new IllegalArgumentException("Illegal Marktplace : " + marketplaceName);
        }
        return "index";
    }

    private MarketplaceAuthorizations createNewMarketplaceAuthorization(LiberecoUser liberecoUser, Marketplace marketplace, EbayToken ebayToken) {
        MarketplaceAuthorizations marketplaceAuthorization = new MarketplaceAuthorizations();
        marketplaceAuthorization.setExpirationTime(ebayToken.getExpirationTime().getTime());
        marketplaceAuthorization.setLiberecoUser(liberecoUser);
        marketplaceAuthorization.setToken(ebayToken.getToken());
        marketplaceAuthorization.setTokenSecret(null);
        marketplaceAuthorization.setKey(new MarketplaceAuthorizationsCompositeKey(liberecoUser.getId(), marketplace.getId()));
        return marketplaceAuthorization;
    }
}
