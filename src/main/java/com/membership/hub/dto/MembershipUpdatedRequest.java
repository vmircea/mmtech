package com.membership.hub.dto;

import com.membership.hub.model.membership.MemberProfession;
import com.membership.hub.model.membership.MemberStatus;
import com.membership.hub.model.shared.ContactInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.membership.hub.model.shared.Pattern.BRANCH_ID;

public class MembershipUpdatedRequest {
    @NotNull
    @NotBlank
    private String id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Pattern(regexp = BRANCH_ID, message = "not a valid branch id")
    private String branchId;
    private int age;
    private MemberStatus status;
    private MemberProfession profession;
    @Valid
    private ContactInfo contactInfo;

    public String getId() {
        return id;
    }
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
