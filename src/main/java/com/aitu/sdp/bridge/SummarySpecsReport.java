package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;

public class SummarySpecsReport extends ComputerReport {
    public SummarySpecsReport(ReportExporter exporter) {
        super(exporter);
    }

    @Override
    public String generate(Computer computer) {
        return generateReport("Summary PC Specifications", computer, ReportDetail.SUMMARY);
    }
}
