package sample.controllers.devices;


import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import sample.Main;

import java.io.IOException;

public abstract class Device {
    private Devices name;
    private byte number;
    private Tab tab;
    private AnchorPane anchorPane;
    private byte[] state;

    public Device(Devices name, byte number) {
        this.name = name;
        this.number = number;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public Devices getName() {
        return name;
    }

    public void setName(Devices name) {
        this.name = name;
    }

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public abstract byte[] getState();

    public void setState(byte[] state) {
        this.state = state;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

   public Tab getTab() {
        Tab tab = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.patern);
        try {
            anchorPane = fxmlLoader.load();
            tab = new Tab("УПТ №" + number, anchorPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
