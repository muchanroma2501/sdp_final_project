package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.GamingPCOrderFactory;

/** Standalone demonstration; it is intentionally not run during web application startup. */
public class BridgePatternDemo {
    public static void main(String[] args) {
        Computer computer = new GamingPCOrderFactory().orderComputer();
        ComputerReport report = new SummarySpecsReport(new JsonReportExporter());

        System.out.println("Bridge pattern demo - JSON exporter:");
        System.out.println(report.generate(computer));

        report.setExporter(new HtmlReportExporter());
        System.out.println("Bridge pattern demo - switched to HTML exporter:");
        System.out.println(report.generate(computer));
    }
}
