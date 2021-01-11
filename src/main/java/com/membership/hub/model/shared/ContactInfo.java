package com.membership.hub.model.shared;

public class ContactInfo {
    private int id;
    private String phoneNumber;
    private String emailAddress;
    private String country;
    private String city;
    private String street; //Name and number
    private String building; //Name or number

    public ContactInfo() {}

    public ContactInfo(int id, String phoneNumber, String emailAddress, String country, String city, String street, String building) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.country = country;
        this.city = city;
        this.street = street;
        this.building = building;
    }

    public ContactInfo(String phoneNumber, String emailAddress, String country, String city, String street, String building) {
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.country = country;
        this.city = city;
        this.street = street;
        this.building = building;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void update(ContactInfo contactInfo) {
        if (contactInfo != null) {
            phoneNumber = contactInfo.getPhoneNumber();
            emailAddress = contactInfo.getPhoneNumber();
            country = contactInfo.getCountry();
            city = contactInfo.getCity();
            street = contactInfo.getStreet();
            building = contactInfo.getBuilding();
        }
    }

    public void patch(ContactInfo contactInfo) {
        if (contactInfo != null) {
            if (contactInfo.getPhoneNumber() != null) {
                phoneNumber = contactInfo.getPhoneNumber();
            }
            if (contactInfo.getEmailAddress() != null) {
                emailAddress = contactInfo.getPhoneNumber();
            }
            if (contactInfo.getCountry() != null) {
                country = contactInfo.getCountry();
            }
            if (contactInfo.getCity() != null) {
                city = contactInfo.getCity();
            }
            if (contactInfo.getStreet() != null) {
                street = contactInfo.getStreet();
            }
            if (contactInfo.getBuilding() != null) {
                building = contactInfo.getBuilding();
            }
        }
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                '}';
    }
}
