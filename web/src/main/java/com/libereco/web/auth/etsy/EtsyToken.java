package com.libereco.web.auth.etsy;

public class EtsyToken {
	
	private String accessToken;

	private String tokenSecret;

	public EtsyToken(String accessToken, String tokenSecret) {
		this.accessToken = accessToken;
		this.tokenSecret = tokenSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
}