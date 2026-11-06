package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;

public class  FullDiagnosticReport extends ComputerReport {
    public FullDiagnosticReport(ReportExporter exporter) {
        super(exporter);
    }

    @Override
    public String generate(Computer computer) {
        return generateReport("Full PC Diagnostic Report", computer, ReportDetail.FULL_DIAGNOSTIC);
    }
}
