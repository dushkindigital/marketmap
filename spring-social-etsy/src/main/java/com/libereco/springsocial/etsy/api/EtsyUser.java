package com.libereco.springsocial.etsy.api;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = EtsyUserDeserializer.class)
public class EtsyUser {
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("login_name")
    private String loginName;
    
    @JsonProperty("primary_email")
    private String primaryEmail;
    
    @JsonProperty("creation_tsz")
    private Date creationDate;
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getLoginName() {
        return loginName;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getPrimaryEmail() {
        return primaryEmail;
    }
    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    @Override
    public String toString() {
        return "EtsyUser [userId=" + userId + ", loginName=" + loginName + ", primaryEmail=" + primaryEmail + ", creationDate=" + creationDate + "]";
    }

}
