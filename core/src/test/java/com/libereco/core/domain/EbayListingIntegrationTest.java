package com.libereco.core.domain;

import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ActiveProfiles;

@RooIntegrationTest(entity = EbayListing.class)
@ActiveProfiles(profiles = "test")
public class EbayListingIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }
}
