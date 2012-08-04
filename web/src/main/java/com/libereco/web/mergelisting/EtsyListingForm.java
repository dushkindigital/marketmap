package com.libereco.web.mergelisting;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.libereco.core.domain.EtsyWhenMade;
import com.libereco.core.domain.EtsyWhoMade;

public class EtsyListingForm {

    @NotNull
    @Enumerated
    private EtsyWhoMade whoMade;

    @NotNull
    private boolean supply;

    @NotNull
    @Enumerated
    private EtsyWhenMade whenMade;

    public EtsyWhoMade getWhoMade() {
        return whoMade;
    }

    public void setWhoMade(EtsyWhoMade whoMade) {
        this.whoMade = whoMade;
    }

    public boolean isSupply() {
        return supply;
    }

    public void setSupply(boolean supply) {
        this.supply = supply;
    }

    public EtsyWhenMade getWhenMade() {
        return whenMade;
    }

    public void setWhenMade(EtsyWhenMade whenMade) {
        this.whenMade = whenMade;
    }
    
    
}
