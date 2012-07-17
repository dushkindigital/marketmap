package com.libereco.web.auth.etsy;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.libereco.core.exceptions.ExternalServiceException;
import com.libereco.web.auth.MarketplaceAuthorizer;
import com.libereco.web.auth.SignInDetails;


public class EtsyAuthorizer implements MarketplaceAuthorizer {

	private Logger logger = LoggerFactory.getLogger(EtsyAuthorizer.class);

	private String apiKey;

	private String sharedSecret;

	private String requestTokenEndpointUrl;

	private String accessTokenEndpointUrl;

	private String authorizationWebsiteUrl;

	public EtsyAuthorizer(String apiKey, String sharedSecret,
			String requestTokenEndpointUrl, String accessTokenEndpointUrl,
			String authorizationWebsiteUrl) {
		
		this.apiKey = apiKey;
		this.sharedSecret = sharedSecret;
		this.requestTokenEndpointUrl = requestTokenEndpointUrl;
		this.accessTokenEndpointUrl = accessTokenEndpointUrl;
		this.authorizationWebsiteUrl = authorizationWebsiteUrl;
	}

	public SignInDetails sendSignInRequest() throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {

		SignInDetails signInResponse = null;
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(apiKey,
				sharedSecret);

		// TODO: Find out whether we can use this for callback
		//consumer.setAdditionalParameters(additionalParameters);
		
		OAuthProvider provider = new CommonsHttpOAuthProvider(
				requestTokenEndpointUrl, accessTokenEndpointUrl,
				authorizationWebsiteUrl);

		logger.debug("Fetching request token from Etsy");

		// TODO: Callbacks don't seem to be supported
		String authUrl = provider.retrieveRequestToken(consumer,
				OAuth.OUT_OF_BAND);

		// String authUrl = provider.retrieveRequestToken(consumer,
		// "http://libereco.com/etsyAuthCallback");
		signInResponse = new SignInDetails(consumer.getToken(), consumer.getTokenSecret(), authUrl);

		logger.debug("Request token [" + consumer.getToken()
				+ "], token secret [" + consumer.getTokenSecret()
				+ "], signInUrl [" + authUrl + "]");

		
		return signInResponse;
	}

	
	@Override
	public SignInDetails getSignInDetails() throws ExternalServiceException {
		SignInDetails signInDetails = null;
		
		try {
			signInDetails = sendSignInRequest();
		} catch (OAuthMessageSignerException e) {
			processSignInRequestException(e);
		} catch (OAuthNotAuthorizedException e) {
			processSignInRequestException(e);
		} catch (OAuthExpectationFailedException e) {
			processSignInRequestException(e);
		} catch (OAuthCommunicationException e) {
			processSignInRequestException(e);
		}
		
		return signInDetails;
	}

	private void processSignInRequestException(Exception e) throws ExternalServiceException{
		logger.warn("Sign in request failed", e);
		throw new ExternalServiceException("Unable to signin to etsy at this moment. See the developer message for more information.",e);
	}
	
	public EtsyToken getToken(String pin, String requestToken, String requestTokenSecret) throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		
		EtsyToken token = null;
		
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(apiKey,
				sharedSecret);
		
		OAuthProvider provider = new CommonsHttpOAuthProvider(
				requestTokenEndpointUrl, accessTokenEndpointUrl,
				authorizationWebsiteUrl);

		consumer.setTokenWithSecret(requestToken, requestTokenSecret);
		provider.setOAuth10a(true);
		logger.debug("Fetching access token from Etsy, pin [" + pin + "]");
		
		provider.retrieveAccessToken(consumer, pin);

		token = new EtsyToken(consumer.getToken(), consumer.getTokenSecret());
		logger.debug("Access token [" + consumer.getToken()
				+ "], token secret [" + consumer.getTokenSecret() + "]");
		
		return token;
	}

	@Override
	public String toString() {
		return "EtsyAuthorizer [apiKey=" + apiKey + ", sharedSecret="
				+ sharedSecret + ", requestTokenEndpointUrl="
				+ requestTokenEndpointUrl + ", accessTokenEndpointUrl="
				+ accessTokenEndpointUrl + ", authorizationWebsiteUrl="
				+ authorizationWebsiteUrl + "]";
	}
}
