package sample.controllers.devices;


public enum Devices {
    UPT("upt"), UPS("ups"), UDU("udu"), UST("ust"), CONSOLE("console");

    Devices(String key) {
        this.key = key;
    }

    private final String key;

    public String key() {
        return this.key;
    }


}
