package com.rbs.studio.trackless.vpn.dialog;

//import com.anchorfree.partner.api.data.Country;


import unified.vpn.sdk.Country;

public class CountryData {
    @Override
    public String toString() {
        return "CountryData{" +
                "pro=" + pro +
                ", countryvalue=" + countryvalue +
                '}';
    }

    private boolean pro = false;
    private Country countryvalue;

    public boolean isPro() {
        return pro;
    }

    public void setPro(boolean pro) {
        this.pro = pro;
    }

    public Country getCountryvalue() {
        return countryvalue;
    }

    public void setCountryvalue(Country countryvalue) {
        this.countryvalue = countryvalue;
    }

}
