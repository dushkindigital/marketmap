package com.libereco.springsocial.ebay.api.impl;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.libereco.springsocial.ebay.api.EbayCategoryOperations;

public class EbayCategoryTemplate implements EbayCategoryOperations {

    private ApiContext apiContext;

    public EbayCategoryTemplate(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public CategoryType[] getEbayCategories() {
        GetCategoriesCall getCategoriesCall = new GetCategoriesCall(apiContext);
        getCategoriesCall.setDetailLevel(new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL });
        try {
            CategoryType[] categories = getCategoriesCall.getCategories();
            return categories;
        } catch (Exception e) {
            throw new RuntimeException("Not able to get categories from Ebay at this moment.", e);
        }
    }

}
