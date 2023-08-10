package org.example;

public class Address {

    private final String buildingFirmName;
    private final String streetNumber;
    private final String streetName;
    private final String cityName;
    private final String state;
    private final String postCode;

    public String getBuildingFirmName() {
        return buildingFirmName;
    }
    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getState() {
        return state;
    }

    public String getPostCode() {
        return postCode;
    }


    public Address(String buildingFirmName,
                   String streetNumber,
                   String streetName,
                   String cityName,
                   String state,
                   String postCode) {
        this.buildingFirmName = buildingFirmName;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.cityName = cityName;
        this.state = state;
        this.postCode = postCode;
    }


}
