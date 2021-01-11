package com.membership.hub.dto;

import com.membership.hub.model.shared.ContactInfo;

public class BranchRequest {
    private String branchId;
    private String adminId;
    private String branchName;
    private ContactInfo contactInfo;

    public String getBranchId() {
        return branchId;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getBranchName() {
        return branchName;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }
}
