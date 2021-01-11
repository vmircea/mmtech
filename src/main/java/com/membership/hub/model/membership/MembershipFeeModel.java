package com.membership.hub.model.membership;

import java.time.LocalDate;

public class MembershipFeeModel {
    private LocalDate paidInDate;
    private Double paidInAmount;

    public MembershipFeeModel(LocalDate paidInDate, Double paidInAmount) {
        this.paidInDate = paidInDate;
        this.paidInAmount = paidInAmount;
    }

    public MembershipFeeModel(Double paidInAmount) {
        this.paidInDate = LocalDate.now();
        this.paidInAmount = paidInAmount;
    }

    public LocalDate getPaidInDate() {
        return paidInDate;
    }

    public void setPaidInDate(LocalDate paidInDate) {
        this.paidInDate = paidInDate;
    }

    public Double getPaidInAmount() {
        return paidInAmount;
    }

    public void setPaidInAmount(Double paidInAmount) {
        this.paidInAmount = paidInAmount;
    }
}
