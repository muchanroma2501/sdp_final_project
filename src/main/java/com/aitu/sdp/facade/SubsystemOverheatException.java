package com.aitu.sdp.facade;

/**
 * Signals that a thermal check detected unsafe or critically high operating temperatures.
 */
public class SubsystemOverheatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SubsystemOverheatException(String message) {
        super(message);
    }
}
