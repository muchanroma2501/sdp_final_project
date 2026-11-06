package com.aitu.sdp.creational;

public final class Z790_ATX_Motherboard implements Motherboard {
    @Override
    public String getModel() {
        return "Z790 ATX";
    }

    @Override
    public String getSocket() {
        return ComponentCatalog.socketForMotherboard(getModel());
    }
}
