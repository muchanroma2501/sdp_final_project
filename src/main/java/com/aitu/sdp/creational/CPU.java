package com.aitu.sdp.creational;

/** Abstract product for processor components. */
public interface CPU {
    String getModel();

    int getTdpWatts();

    String getSocket();
}
