package com.libereco.core.domain;

public enum EtsyWhoMade {

    I_DID("i_did"), COLLECTIVE("collective"), SOMEONE_ELSE("someone_else");

    private String value;

    EtsyWhoMade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
