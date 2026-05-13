package com.saas.common;

public enum ResultCode {

    SUCCESS(200, "success"),

    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not found"),
    CONFLICT(409, "Conflict"),

    INTERNAL_ERROR(500, "Internal server error"),

    USER_NOT_FOUND(1001, "User not found"),
    USER_PASSWORD_ERROR(1002, "Password error"),
    USER_TOKEN_EXPIRED(1003, "Token expired"),

    PRODUCT_NOT_FOUND(2001, "Product not found"),

    ORDER_NOT_FOUND(3001, "Order not found"),
    ORDER_STATUS_INVALID(3002, "Invalid order status"),

    STOCK_INSUFFICIENT(4001, "Insufficient stock"),

    PAYMENT_FAILED(5001, "Payment failed");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
