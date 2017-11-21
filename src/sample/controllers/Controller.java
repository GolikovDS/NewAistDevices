package sample.controllers;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;
import sample.SerialPort.SerialPortUpt;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable, Observer {


    public ComboBox deviceName;
    public ComboBox deviceNumber;
    public TabPane tabPanel;
    public Button test;
    public static List<Upt> uptList = new ArrayList<>();


    private SerialPortUpt serialPortUpt;

    public void addDeviceOnClick(Event event) {
        switch (deviceName.getSelectionModel().getSelectedIndex()) {
            case 0:
                addUptView();
                uptList.get(uptList.size() - 1).setNumber(Byte.parseByte(deviceNumber.getValue().toString()));
                serialPortUpt.register(uptList.get(uptList.size() - 1));
                break;
            case 1:
                addUpsView();
                uptList.get(uptList.size() - 1).setNumber(Byte.parseByte(deviceNumber.getValue().toString()));
                serialPortUpt.register(uptList.get(uptList.size() - 1));
                break;
            case 2:
                addUduView();
                uptList.get(uptList.size() - 1).setNumber(Byte.parseByte(deviceNumber.getValue().toString()));
                serialPortUpt.register(uptList.get(uptList.size() - 1));
                break;
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceName.setItems(FXCollections.observableArrayList(new ArrayList<String>(Arrays.asList("УПТ", "УПС", "УДУ", "УСТ"))));
        deviceName.getSelectionModel().select(0);
        deviceNumber.setItems(FXCollections.observableArrayList(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32))));
        deviceNumber.getSelectionModel().select(0);

        try {
            serialPortUpt = new SerialPortUpt().getPort("COM2", 9600, 8, "", "1", this, this);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void onClickTest(Event event) {

    }

    public void onClickTest2(Event event) {
    }

    public Tab getTab(int name) {
        AnchorPane anchorPane = null;
        String patch = "";
        String sName = "";
        switch (name) {
            case 0:
                patch = "/sample/views/upt_device.fxml";
                sName = "УПТ №";
                break;
            case 1:
                patch = "/sample/views/ups_device.fxml";
                sName = "УПС №";
                break;
            case 2:
                patch = "/sample/views/udu_device.fxml";
                sName = "УДУ №";
                break;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(patch));
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Tab(sName + deviceNumber.getValue().toString(), anchorPane);
    }

    public void addUptView() {
        tabPanel.getTabs().add(getTab(0));
    }

    public void addUpsView() {
        tabPanel.getTabs().add(getTab(1));
    }

    public void addUduView() {
        tabPanel.getTabs().add(getTab(2));
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
