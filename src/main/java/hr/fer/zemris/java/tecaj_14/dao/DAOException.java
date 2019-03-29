package hr.fer.zemris.java.tecaj_14.dao;

/**
 * Exception that is thrown, often as a masked exception, when an exception of
 * DAO occurs.
 *
 * @author Mario Bobic
 */
public class DAOException extends RuntimeException {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code DAOException} with no detail message and no cause.
     */
    public DAOException() {
        super();
    }

    /**
     * Constructs a {@code DAOException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code DAOException} with the specified cause.
     *
     * @param cause the cause
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@code DAOException} with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@code DAOException} with the specified detail message,
     * cause, suppression enabled or disabled, and writable stack trace enabled
     * or disabled.
     *
     * @param message the detail message.
     * @param cause the cause (A {@code null} value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled
     * @param writableStackTrace whether or not the stack trace should be
     *                           writable
     *
     * @since 1.7
     */
    public DAOException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
