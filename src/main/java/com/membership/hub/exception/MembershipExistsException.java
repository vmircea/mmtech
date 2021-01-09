package com.membership.hub.exception;

public class MembershipExistsException extends RuntimeException{
    public MembershipExistsException(String msg) {
        super(msg);
    }
}
