package com.libereco.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.repository.MarketplaceAuthorizationsRepository;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@RooIntegrationTest(entity = MarketplaceAuthorizations.class)
@ActiveProfiles(profiles="local")
public class MarketplaceAuthorizationsIntegrationTest {

	@Autowired
	MarketplaceAuthorizationsRepository marketplaceAuthorizationsRepository;

	@Test
	public void shouldPersistMarketPlaceAuthorizationsAndFindItBack() {
		saveMarketPlaceAuthorization();

		int count = marketplaceAuthorizationsRepository.findAll().size();
		Assert.assertEquals(1, count);
	}

	@Test
	public void shouldCountThePersistedMarketPlaceAuthorizations() {
		saveMarketPlaceAuthorization();
		long count = marketplaceAuthorizationsRepository
				.countMarketplaceAuthorizations();
		assertEquals(1, count);
	}

	@Test
	public void shouldSaveAndFlush() {
		MarketplaceAuthorizations newMarketplace = createNewMarketPlace(Long
				.valueOf(1), Long.valueOf(2));
		MarketplaceAuthorizations marketplaceAuthorizations = marketplaceAuthorizationsRepository
				.saveAndFlush(newMarketplace);
		assertNotNull(marketplaceAuthorizations);
	}

	@Test
	public void shouldDeleteMarketplaceAuthorizations() {
		saveMarketPlaceAuthorization();
		long count = marketplaceAuthorizationsRepository
				.countMarketplaceAuthorizations();
		assertEquals(1, count);
		marketplaceAuthorizationsRepository
				.delete(new MarketplaceAuthorizationsCompositeKey(Long
						.valueOf(1), Long.valueOf(2)));
		count = marketplaceAuthorizationsRepository
				.countMarketplaceAuthorizations();
		assertEquals(0, count);
	}

	@Test
	public void shouldDeleteAllMarketplaceAuthorizations() {
		List<MarketplaceAuthorizations> authorizations = new ArrayList<MarketplaceAuthorizations>();
		for (int i = 0; i < 10; i++) {
			authorizations.add(createNewMarketPlace(i, i + 1));
		}

		marketplaceAuthorizationsRepository.save(authorizations);
		long count = marketplaceAuthorizationsRepository
				.countMarketplaceAuthorizations();
		assertEquals(10, count);

		marketplaceAuthorizationsRepository.deleteAll();
		count = marketplaceAuthorizationsRepository
				.countMarketplaceAuthorizations();
		assertEquals(0, count);
	}
	
	

	private void saveMarketPlaceAuthorization() {
		MarketplaceAuthorizations marketplaceAuthorizations = createNewMarketPlace(
				Long.valueOf(1), Long.valueOf(2));

		marketplaceAuthorizationsRepository.save(marketplaceAuthorizations);
	}

	private MarketplaceAuthorizations createNewMarketPlace(long userId,
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