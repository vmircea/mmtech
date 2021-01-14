package com.membership.hub.exception;

public class PaymentException extends  RuntimeException {

    public enum PaymentErrors {
        BAD_CREDENTIALS,
        BRANCH_HAS_NOT_ENOUGH_MONEY_FOR_TRANSACTION,
        PAYMENT_COULD_NOT_BE_PROCESSED
    }

    private PaymentErrors error;

    private PaymentException(PaymentErrors error) {
        this.error = error;
    }

    public PaymentErrors getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.name().toUpperCase();
    }

    public static PaymentException badCredentials() {
        return new PaymentException(PaymentErrors.BAD_CREDENTIALS);
    }

    public static PaymentException branchHasNotEnoughMoneyForTransaction() {
        return new PaymentException(PaymentErrors.BRANCH_HAS_NOT_ENOUGH_MONEY_FOR_TRANSACTION);
    }

    public static PaymentException paymentCouldNotBeProcessed() {
        return new PaymentException(PaymentErrors.PAYMENT_COULD_NOT_BE_PROCESSED);
    }
}
