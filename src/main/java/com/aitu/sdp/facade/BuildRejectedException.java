package com.aitu.sdp.facade;

import com.aitu.sdp.facade.diagnostics.BuildDiagnostics;

public class BuildRejectedException extends RuntimeException {
    private final BuildDiagnostics diagnostics;

    public BuildRejectedException(BuildDiagnostics diagnostics) {
        super("Конфигурация содержит ошибки совместимости и не может быть собрана.");
        this.diagnostics = diagnostics;
    }

    public BuildDiagnostics getDiagnostics() {
        return diagnostics;
    }
}
