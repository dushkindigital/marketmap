package com.libereco.core.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@ActiveProfiles(profiles = "test")
public class MarketplaceAuthorizationsRepositoryTest {

    private static final Long USER_ID = Long.valueOf(1);

    @Autowired
    MarketplaceAuthorizationsRepository marketplaceAuthorizationsRepository;

    @Autowired
    LiberecoUserRepository liberecoUserRepository;

    @Test
    public void testCountMarketplaceAuthorizations() {
        saveMarketPlaceAuthorization(USER_ID, Long.valueOf(2));
        assertEquals(1, marketplaceAuthorizationsRepository.countMarketplaceAuthorizations());
    }

    @Test
    public void shouldFindAllMarketplaceAuthorizationsByUserId() {
        saveMarketPlaceAuthorization(USER_ID, Long.valueOf(2));
        List<MarketplaceAuthorizations> allAuthorizations = marketplaceAuthorizationsRepository.findAllMarketplaceAuthorizationsBykeyUserId(USER_ID);
        assertEquals(1, allAuthorizations.size());
    }

    private void saveMarketPlaceAuthorization(Long userId, Long marketplaceId) {
        LiberecoUser user = new LiberecoUser("test_user", "password");
        user.setId(USER_ID);
        liberecoUserRepository.save(user);
        LiberecoUser persistedUser = liberecoUserRepository.findOne(USER_ID);
        MarketplaceAuthorizations marketplaceAuthorizations = createNewMarketPlaceAuthorization(
                USER_ID, marketplaceId);

        marketplaceAuthorizations.setLiberecoUser(persistedUser);
        marketplaceAuthorizationsRepository.save(marketplaceAuthorizations);
    }

    private MarketplaceAuthorizations createNewMarketPlaceAuthorization(long userId,
            long marketplaceId) {
        MarketplaceAuthorizations marketplaceAuthorizations = new MarketplaceAuthorizations();
        marketplaceAuthorizations.setExpirationTime(new Date());
        marketplaceAuthorizations.setToken("abc");
        marketplaceAuthorizations.setTokenSecret("fvgvgb");
        marketplaceAuthorizations.setVersion(1);

        marketplaceAuthorizations
                .setKey(new MarketplaceAuthorizationsCompositeKey(userId,
                        marketplaceId));
        return marketplaceAuthorizations;
    }

}
