package com.banking.banking.enums;

public enum ErrorCode {
    VALIDATION_ERROR,
    AUTHENTICATION_FAILED,
    WRONG_USER_NAME_OR_PASSWORD,
    USER_ALREADY_EXISTS,
    ACCOUNT_ALREADY_EXISTS,
    RESOURCE_NOT_FOUND,
    INTERNAL_ERROR,
    ACCESS_DENIED,
    CONFLICT;

    public String getCode() {
        return this.name();
    }
}
