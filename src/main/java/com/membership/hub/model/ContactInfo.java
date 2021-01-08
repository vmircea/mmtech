package com.membership.hub.model;

public class ContactInfo {
    private String phoneNumber;
    private String emailAddress;
    private String country;
    private String city;
    private String street; //Name and number
    private String building; //Name or number

    public ContactInfo(String phoneNumber, String emailAddress, String country, String city, String street, String building) {
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.country = country;
        this.city = city;
        this.street = street;
        this.building = building;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                '}';
    }
}
