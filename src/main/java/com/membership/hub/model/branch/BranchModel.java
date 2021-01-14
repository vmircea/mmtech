package com.membership.hub.model.branch;

import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.membership.Membership;

import java.util.List;

public class BranchModel {
    private String branchId;
    private String adminId;
    private String branchName;
    private ContactInfo contactInfo;
    private Double branchAmount;
    private List<Membership> members;

    public BranchModel(String branchId, String adminId, String branchName, ContactInfo contactInfo) {
        this.branchId = branchId;
        this.adminId = adminId;
        this.branchName = branchName;
        this.contactInfo = contactInfo;
    }

    public BranchModel(String branchId, String adminId, String branchName, ContactInfo contactInfo, Double branchAmount) {
        this.branchId = branchId;
        this.adminId = adminId;
        this.branchName = branchName;
        this.contactInfo = contactInfo;
        this.branchAmount = branchAmount;
    }

    public String getBranchId() {
        return branchId;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getBranchName() {
        return branchName;
    }

    public Double getBranchAmount() {
        return branchAmount;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setBranchAmount(Double branchAmount) {
        this.branchAmount = branchAmount;
    }



    @Override
    public String toString() {
        return "BranchModel{" +
                "branchId='" + branchId + '\'' +
                ", adminId='" + adminId + '\'' +
                ", branchName='" + branchName + '\'' +
                ", contactInfo=" + contactInfo +
                ", branchAmount=" + branchAmount +
                ", members=" + members +
                '}';
    }
}
