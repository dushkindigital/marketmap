package com.libereco.core.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.PendingMarketplaceAuthorizations;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@ActiveProfiles(profiles="local")
public class PendingMarketplaceAuthorizationsRepositoryTest {

    @Autowired
    private PendingMarketplaceAuthorizationsRepository pendingMarketplaceAuthorizationsRepository;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Autowired
    private LiberecoUserRepository liberecoUserRepository;

    @Test
    public void shouldSavePendingMarketPlaceAuthorization() {
        LiberecoUser user = new LiberecoUser("test_user", "password");
        liberecoUserRepository.save(user);
        Marketplace marketplace = new Marketplace("ebay", "ebay");
        marketplaceRepository.save(marketplace);

        user = liberecoUserRepository.findByUsername("test_user");

        marketplace = marketplaceRepository.findByMarketplaceName("ebay");
        PendingMarketplaceAuthorizations entity = new PendingMarketplaceAuthorizations(user, marketplace, "test_token",
                "test_secret_token");
        pendingMarketplaceAuthorizationsRepository.save(entity);
        long count = pendingMarketplaceAuthorizationsRepository.countPendingMarketplaceAuthorizations();
        assertEquals(1, count);
    }

}
