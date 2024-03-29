package com.example.screenshot.zxing;

/**
 * <p>Thrown when an exception occurs during Reed-Solomon decoding, such as when
 * there are too many errors to correct.</p>
 *
 * @author Sean Owen
 */
public final class ReedSolomonException extends Exception {

    public ReedSolomonException(String message) {
        super(message);
    }

}
