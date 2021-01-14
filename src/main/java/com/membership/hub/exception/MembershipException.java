package com.membership.hub.exception;

public class MembershipException extends RuntimeException{
    public enum MembershipErrors {
        MEMBERSHIP_BODY_ID_NOT_EQUAL_WITH_PATH_VARIABLE_ID,
        MEMBERSHIP_ALREADY_EXISTS,
        MEMBERSHIP_NOT_FOUND
    }

    private MembershipException.MembershipErrors error;

    private MembershipException(MembershipException.MembershipErrors error) {
        this.error = error;
    }

    public MembershipException.MembershipErrors getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.name().toUpperCase();
    }

    public static MembershipException membershipBodyIdNotCorrespondsWithPathId() {
        return new MembershipException(MembershipException.MembershipErrors.MEMBERSHIP_BODY_ID_NOT_EQUAL_WITH_PATH_VARIABLE_ID);
    }

    public static MembershipException membershipAlreadyExists() {
        return new MembershipException(MembershipErrors.MEMBERSHIP_ALREADY_EXISTS);
    }

    public static MembershipException membershipNotFound() {
        return new MembershipException(MembershipErrors.MEMBERSHIP_NOT_FOUND);
    }
}
