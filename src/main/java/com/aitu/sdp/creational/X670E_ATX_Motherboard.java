package com.aitu.sdp.creational;

public final class X670E_ATX_Motherboard implements Motherboard {
    @Override
    public String getModel() {
        return "X670E ATX";
    }

    @Override
    public String getSocket() {
        return ComponentCatalog.socketForMotherboard(getModel());
    }
}
