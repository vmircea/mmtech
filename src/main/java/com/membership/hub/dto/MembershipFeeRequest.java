package com.membership.hub.dto;

public class MembershipFeeRequest {
    private String id;
    private Double paidInAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPaidInAmount() {
        return paidInAmount;
    }

    public void setPaidInAmmount(Double paidInAmount) {
        this.paidInAmount = paidInAmount;
    }
}
