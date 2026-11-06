package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;

public interface ReportExporter {
    String exportHeader(String title);

    String exportBody(Computer computer);

    default String exportBody(Computer computer, ReportDetail detail) {
        return exportBody(computer);
    }

    String exportFooter();
}
