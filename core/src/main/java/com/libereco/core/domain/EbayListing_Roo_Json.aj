// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.libereco.core.domain;

import com.libereco.core.domain.EbayListing;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect EbayListing_Roo_Json {
    
    public String EbayListing.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static EbayListing EbayListing.fromJsonToEbayListing(String json) {
        return new JSONDeserializer<EbayListing>().use(null, EbayListing.class).deserialize(json);
    }
    
    public static String EbayListing.toJsonArray(Collection<EbayListing> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<EbayListing> EbayListing.fromJsonArrayToEbayListings(String json) {
        return new JSONDeserializer<List<EbayListing>>().use(null, ArrayList.class).use("values", EbayListing.class).deserialize(json);
    }
    
}
