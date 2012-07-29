package com.libereco.springsocial.etsy.connect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
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
import com.libereco.springsocial.etsy.api.UserOperations;

public class EtsyConnectionFactoryTest {

    private EtsyApi api;
    
    private int listingId = 2058;

    @Before
    public void setupConnection() throws Exception {
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
        api = etsyConnection.getApi();

        assertNotNull(etsyConnection);
        assertTrue(etsyConnection.test());

        UserProfile userProfile = etsyConnection.fetchUserProfile();

        System.out.println(userProfile.getEmail());
        System.out.println(userProfile.getUsername());
        
        UserOperations userOperations = api.userOperations();
        List<EtsyUser> users = userOperations.findAllUsers();
        System.out.println(userOperations.getUserProfile());
    }

    @Test
    public void shouldCreateListing() throws Exception {
        ListingOperations listingOperations = api.listingOperations();
        Listing newListing = ListingBuilder.listing().withShippingTemplateId(260).withDescription("description").withPrice(10)
                .withTitle("test listing" + UUID.randomUUID().toString())
                .withSupply(true).withQuantity(1).withWhenMade("2010_2012").withWhoMade("i_did").withCategoryId(69150467).build();
        Listing createdListing = listingOperations.createListing(newListing);
        System.out.println("Created Listing ** " + createdListing);
    }

    @Test
    public void uploadImage() throws Exception {
        String imageUri = api.listingOperations().uploadListingImage(listingId, "/home/shekhar/dev/dushkin/code/marketmap/spring-social-etsy/src/test/resources/samsung-galaxy-note.jpg");
        System.out.println(imageUri);
    }
    
    @Test
    public void shouldGetImageForListing() throws Exception{
        String imageForListing = api.listingOperations().getImageForListing(listingId, 244597651);
        System.out.println(imageForListing);
    }
    
    @Test
    public void shouldGetAllImagesForListing() throws Exception{
        api.listingOperations().uploadListingImage(listingId, "/home/shekhar/Desktop/documents/openshift-icon.jpg");
        String allListingForImages = api.listingOperations().findAllListingForImages(2058);
        System.out.println(allListingForImages);
    }
    
    @Test
    public void shouldGetListing() throws Exception{
        String listingJson = api.listingOperations().getListing(listingId);
        assertNotNull(listingJson);
    }
    
    @Test
    public void shouldUpdateListing() throws Exception{
        Listing listing = ListingBuilder.listing().withShippingTemplateId(260).withDescription("updateDescription").withPrice(100)
                .withTitle("test listing" + UUID.randomUUID().toString())
                .withSupply(true).withQuantity(1).withWhenMade("2010_2012").withWhoMade("i_did").withCategoryId(69150467).build();
        listing.setListingId(listingId);
        listing.setUserId(14888629);
        listing.setState("active");
        api.listingOperations().updateListing(listing);
    }
    
    @Test
    public void shouldDeleteListing() throws Exception{
        api.listingOperations().deleteListing(listingId);
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
        
        
        // ShippingOperations shippingOperations = api.shippingOperations();
        // String category = shippingOperations.getCategory("accessories");
        // System.out.println("Category : " + category);
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

    }
}
