package sample.controllers;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import sample.controllers.devices.Devices;
import sample.controllers.devices.Upt;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public ComboBox deviceName;
    public ComboBox deviceNumber;
    public TabPane tabPanel;
    Upt upt;

    public void addDeviceOnClick(Event event) {
        upt = new Upt(Devices.UPT, (byte)1, tabPanel);
        upt.onClickAddUpt();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceName.setItems(FXCollections.observableArrayList(new ArrayList<String>(Arrays.asList("УПТ", "УПС", "УДУ", "УСТ"))));
        deviceName.getSelectionModel().select(0);
        deviceNumber.setItems(FXCollections.observableArrayList(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32))));
        deviceNumber.getSelectionModel().select(0);
    }

    public void onClickTest(Event event) {
        System.out.println(Arrays.toString(upt.getState()));
    }
}
