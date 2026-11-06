package com.aitu.sdp.facade;

import com.aitu.sdp.creational.controller.CustomPCRequest;

public record PCReportRequest(String reportType, String format, CustomPCRequest configuration) {
}
