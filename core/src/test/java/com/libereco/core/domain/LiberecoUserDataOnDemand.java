package com.libereco.core.domain;

import com.libereco.core.repository.LiberecoUserRepository;
import com.libereco.core.service.LiberecoUserService;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = LiberecoUser.class)
public class LiberecoUserDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<LiberecoUser> data;

	@Autowired
    LiberecoUserService liberecoUserService;

	@Autowired
    LiberecoUserRepository liberecoUserRepository;

	public LiberecoUser getNewTransientLiberecoUser(int index) {
        LiberecoUser obj = new LiberecoUser();
        setCreated(obj, index);
        setLastUpdated(obj, index);
        setPassword(obj, index);
        setStatus(obj, index);
        setUserName(obj, index);
        return obj;
    }

	public void setCreated(LiberecoUser obj, int index) {
        Date created = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setCreated(created);
    }

	public void setLastUpdated(LiberecoUser obj, int index) {
        Date lastUpdated = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setLastUpdated(lastUpdated);
    }

	public void setPassword(LiberecoUser obj, int index) {
        String password = "password_" + index;
        if (password.length() > 64) {
            password = password.substring(0, 64);
        }
        obj.setPassword(password);
    }

	public void setStatus(LiberecoUser obj, int index) {
        UserStatus status = UserStatus.class.getEnumConstants()[0];
        obj.setStatus(status);
    }

	public void setUserName(LiberecoUser obj, int index) {
        String userName = "userName_" + index;
        obj.setUsername(userName);
    }

	public LiberecoUser getSpecificLiberecoUser(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        LiberecoUser obj = data.get(index);
        Long id = obj.getId();
        return liberecoUserService.findLiberecoUser(id);
    }

	public LiberecoUser getRandomLiberecoUser() {
        init();
        LiberecoUser obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return liberecoUserService.findLiberecoUser(id);
    }

	public boolean modifyLiberecoUser(LiberecoUser obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = liberecoUserService.findLiberecoUserEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'LiberecoUser' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<LiberecoUser>();
        for (int i = 0; i < 10; i++) {
            LiberecoUser obj = getNewTransientLiberecoUser(i);
            try {
                liberecoUserService.saveLiberecoUser(obj);
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            liberecoUserRepository.flush();
            data.add(obj);
        }
    }
}
