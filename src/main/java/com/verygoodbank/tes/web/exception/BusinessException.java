package com.verygoodbank.tes.web.exception;

/**
 * Custom exception for business logic errors.
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message the detail message.
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause (a throwable cause).
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
