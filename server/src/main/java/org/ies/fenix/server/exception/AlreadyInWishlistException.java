package org.ies.fenix.server.exception;

public class AlreadyInWishlistException extends RuntimeException {
    public AlreadyInWishlistException(String message) {
        super(message);
    }
}