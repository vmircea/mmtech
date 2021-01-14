package com.membership.hub.exception;

public class BranchException extends  RuntimeException {
    public enum BranchErrors {
        BRANCH_NOT_FOUND,
        SENDER_BRANCH_AMOUNT_NOT_UPDATED,
        RECEIVER_BRANCH_AMOUNT_NOT_UPDATED,
        WRONG_BRANCH_ID_TYPE,
        BRANCH_ALREADY_EXISTS_WITH_ID,
    }

    private BranchErrors error;

    private BranchException(BranchErrors error) {
        this.error = error;
    }

    public BranchErrors getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.name().toUpperCase();
    }

    public static BranchException branchNotFound() {
        return new BranchException(BranchErrors.BRANCH_NOT_FOUND);
    }

    public static BranchException senderBranchAmountNotUpdated() {
        return new BranchException(BranchErrors.SENDER_BRANCH_AMOUNT_NOT_UPDATED);
    }

    public static BranchException receiverBranchAmountNotUpdated() {
        return new BranchException(BranchErrors.RECEIVER_BRANCH_AMOUNT_NOT_UPDATED);
    }

    public static BranchException wrongBranchIdType() {
        return new BranchException(BranchErrors.WRONG_BRANCH_ID_TYPE);
    }

    public static BranchException branchAlreadyExistsWithThisId() {
        return new BranchException(BranchErrors.BRANCH_ALREADY_EXISTS_WITH_ID);
    }
}
