package com.libereco.springsocial.etsy.connect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;

import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.EtsyUser;
import com.libereco.springsocial.etsy.api.Listing;
import com.libereco.springsocial.etsy.api.ListingBuilder;
import com.libereco.springsocial.etsy.api.ListingOperations;
import com.libereco.springsocial.etsy.api.ShippingOperations;
import com.libereco.springsocial.etsy.api.ShippingTemplateInfo;
import com.libereco.springsocial.etsy.api.ShippingTemplateInfoBuilder;
import com.libereco.springsocial.etsy.api.UserOperations;

public class EtsyConnectionFactoryTest {

    @Test
    public void shouldFetchRequestToken() throws Exception {
        String consumerKey = "2rjd29148344fluxnnqgjmw9";
        String consumerSecret = "3xufvccx7r";
        EtsyConnectionFactory connectionFactory = new EtsyConnectionFactory(consumerKey, consumerSecret);
        OAuth1Operations oAuth1Operations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oAuth1Operations.fetchRequestToken("oob", null);
        assertNotNull(requestToken);
        System.out.println(requestToken.getValue());
        System.out.println(requestToken.getSecret());

        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        String authorizeUrl = oAuth1Operations.buildAuthorizeUrl(requestToken.getValue(), new OAuth1Parameters());
        System.out.println(authorizeUrl);

        System.out.println("Enter the PIN code and hit ENTER when you're done:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pin = br.readLine();

        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, pin);
        OAuthToken accessToken = oAuth1Operations.exchangeForAccessToken(authorizedRequestToken, new OAuth1Parameters(parameters));

        Connection<EtsyApi> etsyConnection = connectionFactory.createConnection(accessToken);
        EtsyApi api = etsyConnection.getApi();

        assertNotNull(etsyConnection);
        assertTrue(etsyConnection.test());

        UserProfile userProfile = etsyConnection.fetchUserProfile();

        System.out.println(userProfile.getEmail());
        System.out.println(userProfile.getUsername());

        UserOperations userOperations = api.userOperations();
        List<EtsyUser> users = userOperations.findAllUsers();
        System.out.println(userOperations.getUserProfile());

//        ShippingOperations shippingOperations = api.shippingOperations();
//        String category = shippingOperations.getCategory("accessories");
//        System.out.println("Category : " + category);
        // ShippingOperations shippingOperations = api.shippingOperations();
        //
        // String paymentTemplates =
        // shippingOperations.findAllUserPaymentTemplates("14888629");
        // System.out.println("Payment Template **** " + paymentTemplates);
        //
        // ShippingTemplateInfo shippingTemplateInfo =
        // ShippingTemplateInfoBuilder.shippingTemplateInfo().withTitle("mytemplate")
        // .withPrimaryCost(10).withSecondaryCost(5).withOriginCountryId(209).withDestinationCountryId(209).build();
        // String createdShippingTemplateInfo =
        // shippingOperations.createShippingTemplate(shippingTemplateInfo);
        //
        // System.out.println("Created Shipping template info *** " +
        // createdShippingTemplateInfo);

        ListingOperations listingOperations = api.listingOperations();
        Listing listing = ListingBuilder.listing().withShippingTemplateId(260).withDescription("description").withPrice(10).withTitle("test listing")
                .withSupply(true).withQuantity(1).withWhenMade("2010_2012").withWhoMade("i_did").withCategoryId(69150467).build();
        String createdListing = listingOperations.createListing(listing);
        System.out.println("Created Listing ** " + createdListing);
    }

    private void commented() {
        // String paymentInfo =
        // "allow_check=true&allow_mo=true&state=new+york&zip=10111&allow_other=true&allow_paypal=true&city=any&country_id=76&first_line=any&name=any&paypal_email=any@example.com&second_line=any";
        //
        // PaymentTemplateInfo paymentTemplateInfo =
        // PaymentTemplateInfoBuilder.paymentTemplateInfo().withAllowCheck(true).withAllowMo(true).withState("new york").withZip("10111")
        // .withAllowOther(true).withAllowPaypal(true).withCity("any").withCountryId(766).withFirstLine("any").withName("any")
        // .withPaypalEmail("any@example.com").withSecondLine("any").build();
        // System.out.println(shippingOperations.createPaymentTemplate(paymentTemplateInfo));
        // String countries = shippingOperations.getCountries();
        //
        // System.out.println("Countries --------------- " + countries);
        // ShippingTemplateInfo shippingTemplateInfo =
        // ShippingTemplateInfoBuilder.shippingTemplateInfo().withTitle("test")
        // .withPrimaryCost(10).withSecondaryCost(5).withOriginCountryId(209).withDestinationCountryId(209).build();
        // ShippingTemplateInfo createdShippingTemplateInfo =
        // shippingOperations.createShippingTemplate(shippingTemplateInfo);
        //
        // System.out.println(createdShippingTemplateInfo);
        // // ListingOperations listingOperations = api.listingOperations();
        // Listing listing = new Listing();
        // listingOperations.createListing(listing);

    }
}
