package com.libereco.core.domain;

public enum EtsyWhenMade {

    MADE_TO_ORDER("made_to_order"), YEAR_2010_2012("2010_2012"), YEAR_2000_2009("2000_2009"), YEAR_1993_1999("1993_1999"), YEAR_BEFORE_1993(
            "before_1993"), YEAR_1990_1992("1990_1992"), YEAR_1980S("1980s"), YEAR_1970S("1970s"), YEAR_1960S("1960s"), YEAR_1950S("1950s"), YEAR_1940S(
            "1940s"), YEAR_1930S("1930s"), YEAR_1920S("1920s"), YEAR_1910S("1910s"), YEAR_1900S("1900s"), YEAR_1800S("1800s"), YEAR_1700S("1700s"), YEAR_BEFORE_1700(
            "before_1700");

    private String value;

    private EtsyWhenMade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
