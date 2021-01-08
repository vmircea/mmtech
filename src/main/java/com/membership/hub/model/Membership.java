package com.membership.hub.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Membership {
    private String id;
    private String name;
    private int age;
    private MemberStatus status;
    private MemberProfession profession;
    private ContactInfo contactInfo;
    private List<MemberSkill> skills;
    private HashMap<LocalDate, Long> paidInFeeDetails;

    public Membership(String name, int age, MemberStatus status, MemberProfession profession, ContactInfo contactInfo) {
        this.name = name;
        this.age = age;
        this.status = status;
        this.profession = profession;
        this.contactInfo = contactInfo;
    }

    public Membership(String id, String name, int age, MemberStatus status, MemberProfession profession, ContactInfo contactInfo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.status = status;
        this.profession = profession;
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Membership{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", status=" + status +
                ", profession=" + profession +
                ", contactInfo=" + contactInfo.toString()+
                '}';
    }
}
