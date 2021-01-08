package com.membership.hub.dto;

import com.membership.hub.model.ContactInfo;
import com.membership.hub.model.MemberProfession;
import com.membership.hub.model.MemberStatus;

public class MembershipAddedRequest {
    private String name;
    private int age;
    private MemberStatus status;
    private MemberProfession profession;
    private ContactInfo contactInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public MemberProfession getProfession() {
        return profession;
    }

    public void setProfession(MemberProfession profession) {
        this.profession = profession;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
}
