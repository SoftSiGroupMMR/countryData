package dk.si.countryData.classes;

public class CountryData {
    String countryCode, countryName, flagUrl, countryCurrency;

    public CountryData(String countryCode, String countryName, String flagUrl, String countryCurrency) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.flagUrl = flagUrl;
        this.countryCurrency = countryCurrency;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getCountryCurrency() {
        return countryCurrency;
    }

    public void setCountryCurrency(String countryCurrency) {
        this.countryCurrency = countryCurrency;
    }
}
