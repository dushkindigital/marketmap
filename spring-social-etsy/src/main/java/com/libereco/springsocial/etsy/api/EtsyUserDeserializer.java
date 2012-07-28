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

public class EtsyUserDeserializer extends JsonDeserializer<EtsyUser> {

    @Override
    public EtsyUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode tree = jp.readValueAsTree();
        ArrayNode resultsNode = (ArrayNode) tree.get("results");
        Iterator<JsonNode> elements = resultsNode.getElements();
        JsonNode userNode = elements.hasNext() ? elements.next() : null;
        int userId = userNode.get("user_id").getValueAsInt();
        String loginName = userNode.get("login_name") != null ? userNode.get("login_name").getTextValue() : null;
        String primaryEmail = userNode.get("primary_email") != null ? userNode.get("primary_email").getTextValue() : null;
        Date creationDate = new Date(userNode.get("creation_tsz") != null ? userNode.get("creation_tsz").getLongValue() : null);
        return EtsyUserBuilder.etsyUser().withLoginName(loginName).withPrimaryEmail(primaryEmail).withCreationDate(creationDate).withUserId(String.valueOf(userId))
                .build();
    }

}
