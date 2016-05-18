package com.example.ctfung.exchangerateproject;

/**
 * Created by CTFung on 14/5/16.
 */
public class Country {
    private int id,flagId;
    private String currencyName, convertedMoney, convertedRate;

    public Country(int id, int flagId, String currencyName, String convertedMoney, String convertedRate) {
        this.id = id;
        this.flagId = flagId;
        this.currencyName = currencyName;
        this.convertedMoney = convertedMoney;
        this.convertedRate = convertedRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlagId() {
        return flagId;
    }

    public void setFlagId(int flagId) {
        this.flagId = flagId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getConvertedMoney() {
        return convertedMoney;
    }

    public void setConvertedMoney(String convertedMoney) {
        this.convertedMoney = convertedMoney;
    }

    public String getConvertedRate() {
        return convertedRate;
    }

    public void setConvertedRate(String convertedRate) {
        this.convertedRate = convertedRate;
    }
}
