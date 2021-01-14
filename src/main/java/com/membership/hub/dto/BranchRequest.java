package com.membership.hub.dto;

import com.membership.hub.model.shared.ContactInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.membership.hub.model.shared.Pattern.BRANCH_ID;

public class BranchRequest {
    @NotNull
    @NotBlank
    @Pattern(regexp = BRANCH_ID, message = "not a valid branch id")
    private String branchId;
    private String adminId;
    @NotNull
    @NotBlank
    private String branchName;
    @Valid
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
