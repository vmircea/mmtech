package com.membership.hub.model;

import java.util.List;

public class Branch {
    private String branchId;
    private String branchName;
    private List<Membership> membersList;
    private ContactInfo contactInfo;

    public Branch(String branchId, String branchName) {
        this.branchId = branchId;
        this.branchName = branchName;
    }
}
