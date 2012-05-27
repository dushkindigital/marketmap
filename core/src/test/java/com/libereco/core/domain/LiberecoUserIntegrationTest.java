package com.libereco.core.domain;

import com.libereco.core.repository.LiberecoUserRepository;
import com.libereco.core.service.LiberecoUserService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = LiberecoUser.class)
@ActiveProfiles(profiles="local")
public class LiberecoUserIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private LiberecoUserDataOnDemand dod;

	@Autowired
    LiberecoUserService liberecoUserService;

	@Autowired
    LiberecoUserRepository liberecoUserRepository;

	@Test
    public void testCountAllLiberecoUsers() {
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", dod.getRandomLiberecoUser());
        long count = liberecoUserService.countAllLiberecoUsers();
        Assert.assertTrue("Counter for 'LiberecoUser' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindLiberecoUser() {
        LiberecoUser obj = dod.getRandomLiberecoUser();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to provide an identifier", id);
        obj = liberecoUserService.findLiberecoUser(id);
        Assert.assertNotNull("Find method for 'LiberecoUser' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'LiberecoUser' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllLiberecoUsers() {
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", dod.getRandomLiberecoUser());
        long count = liberecoUserService.countAllLiberecoUsers();
        Assert.assertTrue("Too expensive to perform a find all test for 'LiberecoUser', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<LiberecoUser> result = liberecoUserService.findAllLiberecoUsers();
        Assert.assertNotNull("Find all method for 'LiberecoUser' illegally returned null", result);
        Assert.assertTrue("Find all method for 'LiberecoUser' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindLiberecoUserEntries() {
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", dod.getRandomLiberecoUser());
        long count = liberecoUserService.countAllLiberecoUsers();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<LiberecoUser> result = liberecoUserService.findLiberecoUserEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'LiberecoUser' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'LiberecoUser' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        LiberecoUser obj = dod.getRandomLiberecoUser();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to provide an identifier", id);
        obj = liberecoUserService.findLiberecoUser(id);
        Assert.assertNotNull("Find method for 'LiberecoUser' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyLiberecoUser(obj);
        Integer currentVersion = obj.getVersion();
        liberecoUserRepository.flush();
        Assert.assertTrue("Version for 'LiberecoUser' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testUpdateLiberecoUserUpdate() {
        LiberecoUser obj = dod.getRandomLiberecoUser();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to provide an identifier", id);
        obj = liberecoUserService.findLiberecoUser(id);
        boolean modified =  dod.modifyLiberecoUser(obj);
        Integer currentVersion = obj.getVersion();
        LiberecoUser merged = liberecoUserService.updateLiberecoUser(obj);
        liberecoUserRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'LiberecoUser' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveLiberecoUser() {
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", dod.getRandomLiberecoUser());
        LiberecoUser obj = dod.getNewTransientLiberecoUser(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'LiberecoUser' identifier to be null", obj.getId());
        liberecoUserService.saveLiberecoUser(obj);
        liberecoUserRepository.flush();
        Assert.assertNotNull("Expected 'LiberecoUser' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDeleteLiberecoUser() {
        LiberecoUser obj = dod.getRandomLiberecoUser();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiberecoUser' failed to provide an identifier", id);
        obj = liberecoUserService.findLiberecoUser(id);
        liberecoUserService.deleteLiberecoUser(obj);
        liberecoUserRepository.flush();
        Assert.assertNull("Failed to remove 'LiberecoUser' with identifier '" + id + "'", liberecoUserService.findLiberecoUser(id));
    }
}
