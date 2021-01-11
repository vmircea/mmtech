package com.membership.hub.dto;

import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.membership.MemberProfession;
import com.membership.hub.model.membership.MemberStatus;

public class MembershipAddedRequest {
    private String name;
    private String branchId;
    private int age;
    private MemberStatus status;
    private MemberProfession profession;
    private ContactInfo contactInfo;

    public String getName() {
        return name;
    }
    public String getBranchId() {
        return branchId;
    }
    public int getAge() {
        return age;
    }
    public MemberStatus getStatus() {
        return status;
    }
    public MemberProfession getProfession() {
        return profession;
    }
    public ContactInfo getContactInfo() {
        return contactInfo;
    }
}
