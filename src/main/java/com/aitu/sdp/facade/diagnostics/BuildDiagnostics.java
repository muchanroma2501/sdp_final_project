package com.aitu.sdp.facade.diagnostics;

import java.util.List;

public record BuildDiagnostics(
        DiagnosticStatus status,
        List<DiagnosticCheck> checks,
        Integer requiredPowerWatts,
        Integer recommendedPowerWatts,
        Integer selectedPowerWatts,
        boolean buildAllowed) {
}
