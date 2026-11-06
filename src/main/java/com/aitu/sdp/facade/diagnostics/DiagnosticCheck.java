package com.aitu.sdp.facade.diagnostics;

public record DiagnosticCheck(
        String id,
        String title,
        DiagnosticSeverity severity,
        String message,
        String recommendation) {
}
