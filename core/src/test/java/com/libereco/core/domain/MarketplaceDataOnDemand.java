package com.libereco.core.domain;

import com.libereco.core.repository.MarketplaceRepository;
import com.libereco.core.service.MarketplaceService;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Configurable
@Component
@RooDataOnDemand(entity = Marketplace.class)
public class MarketplaceDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Marketplace> data;

	@Autowired
    MarketplaceService marketplaceService;

	@Autowired
    MarketplaceRepository marketplaceRepository;

	public Marketplace getNewTransientMarketplace(int index) {
        Marketplace obj = new Marketplace();
        setMarketplaceName(obj, index);
        setMarketplaceShortName(obj, index);
        return obj;
    }

	public void setMarketplaceName(Marketplace obj, int index) {
        String marketplaceName = "marketplaceName_" + index;
        obj.setMarketplaceName(marketplaceName);
    }

	public void setMarketplaceShortName(Marketplace obj, int index) {
        String marketplaceShortName = "marketplaceShortName_" + index;
        obj.setMarketplaceShortName(marketplaceShortName);
    }

	public Marketplace getSpecificMarketplace(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Marketplace obj = data.get(index);
        Long id = obj.getId();
        return marketplaceService.findMarketplace(id);
    }

	public Marketplace getRandomMarketplace() {
        init();
        Marketplace obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return marketplaceService.findMarketplace(id);
    }

	public boolean modifyMarketplace(Marketplace obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = marketplaceService.findMarketplaceEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Marketplace' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Marketplace>();
        for (int i = 0; i < 10; i++) {
            Marketplace obj = getNewTransientMarketplace(i);
            try {
                marketplaceService.saveMarketplace(obj);
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            marketplaceRepository.flush();
            data.add(obj);
        }
    }
}
