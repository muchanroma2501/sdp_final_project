package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;

public abstract class ComputerReport {
    protected ReportExporter exporter;

    protected ComputerReport(ReportExporter exporter) {
        setExporter(exporter);
    }

    public void setExporter(ReportExporter exporter) {
        if (exporter == null) {
            throw new IllegalArgumentException("Report exporter cannot be null");
        }
        this.exporter = exporter;
    }

    public abstract String generate(Computer computer);

    protected String generateReport(String title, Computer computer, ReportDetail detail) {
        if (computer == null) {
            throw new IllegalArgumentException("Computer cannot be null");
        }
        return exporter.exportHeader(title)
                + exporter.exportBody(computer, detail)
                + exporter.exportFooter();
    }
}
