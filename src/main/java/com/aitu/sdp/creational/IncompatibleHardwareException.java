package com.aitu.sdp.creational;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Raised when selected hardware cannot be safely powered by the selected PSU. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class IncompatibleHardwareException extends IllegalStateException {
    public IncompatibleHardwareException(String message) {
        super(message);
    }
}
