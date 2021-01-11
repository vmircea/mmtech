package com.membership.hub.model.membership;

import com.membership.hub.model.shared.ContactInfo;

import java.util.List;

public class Membership {
    private String id;
    private String branchId;
    private String name;
    private int age;
    private MemberStatus status;
    private MemberProfession profession;
    private ContactInfo contactInfo;
    private List<MemberSkill> skills;
    private List<MembershipFeeModel> paidInFeeDetails;

    public Membership(String name, String branchId, int age, MemberStatus status, MemberProfession profession, ContactInfo contactInfo) {
        this.name = name;
        this.branchId = branchId;
        this.age = age;
        this.status = status;
        this.profession = profession;
        this.contactInfo = contactInfo;
    }

    public Membership(String id, String branchId, String name, int age, MemberStatus status, MemberProfession profession, ContactInfo contactInfo) {
        this.id = id;
        this.name = name;
        this.branchId = branchId;
        this.age = age;
        this.status = status;
        this.profession = profession;
        this.contactInfo = contactInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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

    public List<MemberSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<MemberSkill> skills) {
        this.skills = skills;
    }

    public List<MembershipFeeModel> getPaidInFeeDetails() {
        return paidInFeeDetails;
    }

    public void setPaidInFeeDetails(List<MembershipFeeModel> paidInFeeDetails) {
        this.paidInFeeDetails = paidInFeeDetails;
    }

    public void update (Membership membership) {
        if (membership != null) {
            name = membership.getName();
            age = membership.getAge();
            status = membership.getStatus();
            profession = membership.getProfession();
            contactInfo.update(membership.getContactInfo());
        }
    }

    public void patch (Membership membership) {
        if (membership != null) {
            contactInfo.patch(membership.contactInfo);
            if (membership.getName() != null) {
                name = membership.getName();
            }
            else if (membership.getAge() != 0) {
                age = membership.getAge();
            }
            else if (membership.getStatus() != null) {
                status = membership.getStatus();
            }
            else if (membership.getProfession() != null) {
                profession = membership.getProfession();
            }
            else if(membership.contactInfo != null) {
                contactInfo.patch(membership.getContactInfo());
            }
        }
    }

    @Override
    public String toString() {
        return "Membership{" +
                "id='" + id + '\'' +
                ", branchId='" + branchId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", status=" + status +
                ", profession=" + profession +
                ", contactInfo=" + contactInfo +
                ", skills=" + skills +
                ", paidInFeeDetails=" + paidInFeeDetails +
                '}';
    }
}
