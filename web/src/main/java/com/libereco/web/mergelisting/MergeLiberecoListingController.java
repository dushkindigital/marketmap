package com.libereco.web.mergelisting;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;
import com.libereco.core.service.MarketplaceService;
import com.libereco.web.security.SecurityUtils;

@Controller
@RequestMapping("/listings")
public class MergeLiberecoListingController {

    private LiberecoUserService liberecoUserService;
    private MarketplaceAuthorizationsService marketplaceAuthorizationsService;
    private MarketplaceService marketplaceService;

    @Inject
    public MergeLiberecoListingController(LiberecoUserService liberecoUserService, MarketplaceAuthorizationsService marketplaceAuthorizationsService,
            MarketplaceService marketplaceService) {
        this.liberecoUserService = liberecoUserService;
        this.marketplaceAuthorizationsService = marketplaceAuthorizationsService;
        this.marketplaceService = marketplaceService;
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        String loggedInUser = SecurityUtils.getCurrentLoggedInUsername();
        LiberecoUser user = liberecoUserService.findUserByUsername(loggedInUser);
        List<MarketplaceAuthorizations> allMarketplaceAuthorizations = marketplaceAuthorizationsService.findAllMarketplaceAuthorizationsForUser(user
                .getId());
        if (CollectionUtils.isEmpty(allMarketplaceAuthorizations)) {
            return "listings/error";
        }

        LiberecoListingForm liberecoListingForm = new LiberecoListingForm();
        for (MarketplaceAuthorizations marketplaceAuthorizations : allMarketplaceAuthorizations) {
            if ("ebay".equals(marketplaceService.findMarketplace(marketplaceAuthorizations.getKey().getMarketplaceId()).getMarketplaceName())) {
                liberecoListingForm.setEbayListingForm(new EbayListingForm());
            }
            if ("etsy".equals(marketplaceService.findMarketplace(marketplaceAuthorizations.getKey().getMarketplaceId()).getMarketplaceName())) {
                liberecoListingForm.setEtsyListingForm(new EtsyListingForm());
            }
        }
        return "listings/create";
    }
}
