package com.libereco.springsocial.ebay.api;

import java.util.Date;

public class EbayUser {

    private String userId;

    private String loginName;

    private String primaryEmail;

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
