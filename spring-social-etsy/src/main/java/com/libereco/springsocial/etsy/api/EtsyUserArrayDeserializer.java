package com.libereco.springsocial.etsy.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.ArrayNode;

public class EtsyUserArrayDeserializer extends JsonDeserializer<EtsyUserCollection> {

    @Override
    public EtsyUserCollection deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        EtsyUserCollection etsyUserCollection = new EtsyUserCollection();
        List<EtsyUser> etsyUsers = new ArrayList<EtsyUser>();
        JsonNode tree = jp.readValueAsTree();
        ArrayNode resultsNode = (ArrayNode) tree.get("results");
        Iterator<JsonNode> elements = resultsNode.getElements();
        while (elements.hasNext()) {
            JsonNode userNode = elements.next();
            String userId = userNode.get("user_id") != null ? userNode.get("user_id").getTextValue() : null;
            String loginName = userNode.get("login_name") != null ? userNode.get("login_name").getTextValue() : null;
            String primaryEmail = userNode.get("primary_email") != null ? userNode.get("primary_email").getTextValue() : null;
            Date creationDate = new Date(userNode.get("creation_tsz") != null ? userNode.get("creation_tsz").getLongValue() : null);
            EtsyUser etsyUser = EtsyUserBuilder.etsyUser().
                    withLoginName(loginName).
                    withPrimaryEmail(primaryEmail).
                    withCreationDate(creationDate).
                    withUserId(userId).
                    build();
            etsyUsers.add(etsyUser);
        }
        etsyUserCollection.setEtsyUsers(etsyUsers);
        return etsyUserCollection;
    }

}
