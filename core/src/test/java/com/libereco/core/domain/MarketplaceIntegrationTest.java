package com.libereco.core.domain;

import com.libereco.core.repository.MarketplaceRepository;
import com.libereco.core.service.MarketplaceService;
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

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@RooIntegrationTest(entity = Marketplace.class)
@ActiveProfiles(profiles="test")
public class MarketplaceIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private MarketplaceDataOnDemand dod;

	@Autowired
    MarketplaceService marketplaceService;

	@Autowired
    MarketplaceRepository marketplaceRepository;

	@Test
    public void testCountAllMarketplaces() {
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", dod.getRandomMarketplace());
        long count = marketplaceService.countAllMarketplaces();
        Assert.assertTrue("Counter for 'Marketplace' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindMarketplace() {
        Marketplace obj = dod.getRandomMarketplace();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to provide an identifier", id);
        obj = marketplaceService.findMarketplace(id);
        Assert.assertNotNull("Find method for 'Marketplace' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Marketplace' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllMarketplaces() {
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", dod.getRandomMarketplace());
        long count = marketplaceService.countAllMarketplaces();
        Assert.assertTrue("Too expensive to perform a find all test for 'Marketplace', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Marketplace> result = marketplaceService.findAllMarketplaces();
        Assert.assertNotNull("Find all method for 'Marketplace' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Marketplace' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindMarketplaceEntries() {
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", dod.getRandomMarketplace());
        long count = marketplaceService.countAllMarketplaces();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Marketplace> result = marketplaceService.findMarketplaceEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Marketplace' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Marketplace' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Marketplace obj = dod.getRandomMarketplace();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to provide an identifier", id);
        obj = marketplaceService.findMarketplace(id);
        Assert.assertNotNull("Find method for 'Marketplace' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyMarketplace(obj);
        Integer currentVersion = obj.getVersion();
        marketplaceRepository.flush();
        Assert.assertTrue("Version for 'Marketplace' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testUpdateMarketplaceUpdate() {
        Marketplace obj = dod.getRandomMarketplace();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to provide an identifier", id);
        obj = marketplaceService.findMarketplace(id);
        boolean modified =  dod.modifyMarketplace(obj);
        Integer currentVersion = obj.getVersion();
        Marketplace merged = marketplaceService.updateMarketplace(obj);
        marketplaceRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Marketplace' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveMarketplace() {
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", dod.getRandomMarketplace());
        Marketplace obj = dod.getNewTransientMarketplace(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Marketplace' identifier to be null", obj.getId());
        marketplaceService.saveMarketplace(obj);
        marketplaceRepository.flush();
        Assert.assertNotNull("Expected 'Marketplace' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDeleteMarketplace() {
        Marketplace obj = dod.getRandomMarketplace();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Marketplace' failed to provide an identifier", id);
        obj = marketplaceService.findMarketplace(id);
        marketplaceService.deleteMarketplace(obj);
        marketplaceRepository.flush();
        Assert.assertNull("Failed to remove 'Marketplace' with identifier '" + id + "'", marketplaceService.findMarketplace(id));
    }
}
