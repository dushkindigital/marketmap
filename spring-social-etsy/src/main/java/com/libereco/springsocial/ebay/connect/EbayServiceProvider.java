package com.libereco.springsocial.ebay.connect;

import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

import com.ebay.sdk.ApiContext;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.ebay.api.impl.EbayTemplate;

public class EbayServiceProvider implements OAuth1ServiceProvider<EbayApi> {

    private EbayOAuth1Template ebayOAuth1Template;
    private ApiContext apiContext;

    public EbayServiceProvider(String ruName, String signInUrl, ApiContext apiContext) {
        ebayOAuth1Template = new EbayOAuth1Template(ruName, signInUrl, apiContext);
        this.apiContext = apiContext;
    }

    @Override
    public EbayApi getApi(String accessToken, String secret) {
        apiContext.getApiCredential().seteBayToken(accessToken);
        return new EbayTemplate(apiContext);
    }

    @Override
    public OAuth1Operations getOAuthOperations() {
        return ebayOAuth1Template;
    }

}
