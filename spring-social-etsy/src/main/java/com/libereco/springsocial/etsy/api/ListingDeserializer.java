package com.libereco.springsocial.etsy.api;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.ArrayNode;

public class ListingDeserializer extends JsonDeserializer<EtsyListing> {

    @Override
    public EtsyListing deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode tree = jp.readValueAsTree();
        ArrayNode resultsNode = (ArrayNode) tree.get("results");
        Iterator<JsonNode> elements = resultsNode.getElements();
        JsonNode listingNode = elements.hasNext() ? elements.next() : null;
        if (listingNode == null) {
            return null;
        }
        ArrayNode shippingInfoArrayNode = (ArrayNode)listingNode.get("ShippingInfo");
        JsonNode shippingInfoNode = shippingInfoArrayNode.getElements().hasNext() ?shippingInfoArrayNode.getElements().next() : null; 
        
        EtsyListing listing = ListingBuilder.listing().
                withCategoryId(listingNode.get("category_id").getIntValue()).
                withDescription(listingNode.get("description").getValueAsText()).
                withListingId(listingNode.get("listing_id").getIntValue()).
                withPrice(listingNode.get("price").getDoubleValue()).
                withQuantity(listingNode.get("quantity").getIntValue()).
                withState(listingNode.get("state").getTextValue()).
                withSupply(listingNode.get("is_supply").getBooleanValue()).
                withTitle(listingNode.get("title").getTextValue()).
                withUserId(listingNode.get("user_id").getIntValue()).
                withWhenMade(listingNode.get("when_made").getTextValue()).
                withCreationDate(new Date(listingNode.get("creation_tsz").getLongValue())).
                withCurrencyCode(listingNode.get("currency_code").getTextValue()).
                withEndingDate(new Date(listingNode.get("ending_tsz").getLongValue())).
                withUrl(listingNode.get("url").getTextValue()).
                withWhoMade(listingNode.get("who_made").getTextValue()).
                withShippingTemplateId(shippingInfoNode.get("shipping_info_id").getIntValue()).
                build();
        return listing;

    }
}
