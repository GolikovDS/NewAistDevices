package sample.controllers.devices;


import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Upt extends Device implements Initializable, Observer {

    public CheckBox error_power;
    public CheckBox error_akb;
    public CheckBox upt_is_blocked;
    public CheckBox door_open;
    public CheckBox upt_fire1;
    public CheckBox upt_fire2;
    public CheckBox error;
    public CheckBox avt_off;
    public ChoiceBox P1;
    public ChoiceBox sh2;
    public ChoiceBox sh1;
    public ChoiceBox sh3;
    public ChoiceBox vu;
    public ChoiceBox sdu;
    public ChoiceBox kva;
    public ChoiceBox gu;
    public ChoiceBox gn;
    public ChoiceBox ao;
    public ChoiceBox ob1;
    public ChoiceBox ykd;
    public ChoiceBox kdp;
    public TextField сondown;
    public TextField u_power;
    public TextField u_power_akb;

    public TabPane tansPanelUpt;

    public Upt(Devices name, byte number, TabPane tabPane) {
        super(name, number);
        tansPanelUpt = tabPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] s = {"Норма","Пуск", "КЗ", "Обрыв"};
        String[] sSh = {"Норма","Норма", "КЗ", "Обрыв", "Пожар1", "Пожар2"};
        P1.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        sh1.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sh2.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sh3.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sdu.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        vu.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        kva.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        gu.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        gn.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ao.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ob1.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ykd.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        kdp.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        kva.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        P1.getSelectionModel().select(0);
        sh1.getSelectionModel().select(0);
        sh2.getSelectionModel().select(0);
        sh3.getSelectionModel().select(0);
        sdu.getSelectionModel().select(0);
        vu.getSelectionModel().select(0);
        gu.getSelectionModel().select(0);
        gn.getSelectionModel().select(0);
        ao.getSelectionModel().select(0);
        ob1.getSelectionModel().select(0);
        ykd.getSelectionModel().select(0);
        kdp.getSelectionModel().select(0);
        kva.getSelectionModel().select(0);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public byte[] getState() {
        return new byte[]{1, 3, 10,
                0x01,
                getByteState_upt(),
                getState_sh1_sh2(),
                getState_sh3_P1(),
                getTablo(),
                getUKD(),
                (byte) (Double.parseDouble(сondown.getText()) * 10 - 100),
                (byte) (Double.parseDouble(u_power.getText()) * 10 - 100),
                (byte) (Double.parseDouble(u_power_akb.getText()) * 10 - 100),
                0x00};
    }

    public void onClickAddUpt() {
        tansPanelUpt.getTabs().add(getTab());
    }


    private byte getByteState_upt() {
        byte b = 0x00;
        if (error.isSelected())
            b = 0x01;
        if (error_power.isSelected())
            b |= 0x02;
        if (error_akb.isSelected())
            b |= 0x04;
        if (door_open.isSelected())
            b |= 0x08;
        if (upt_is_blocked.isSelected())
            b |= 0x10;
        if (upt_fire1.isSelected())
            b |= 0x20;
        if (upt_fire2.isSelected())
            b |= 0x40;
        if (avt_off.isSelected())
            b |= 0x80;
        return b;
    }

    private byte getState_sh1_sh2() {
        return (byte) (sh1.getSelectionModel().getSelectedIndex() | sh2.getSelectionModel().getSelectedIndex() << 3 |
                P1.getSelectionModel().getSelectedIndex() << 6);
    }

    private byte getState_sh3_P1() {
        return (byte) (sh3.getSelectionModel().getSelectedIndex() | vu.getSelectionModel().getSelectedIndex() << 3 |
                sdu.getSelectionModel().getSelectedIndex() << 5);
    }

    private byte getTablo() {
        return (byte) (ao.getSelectionModel().getSelectedIndex() | gn.getSelectionModel().getSelectedIndex() << 2 |
                gu.getSelectionModel().getSelectedIndex() << 4 | kva.getSelectionModel().getSelectedIndex() << 6);
    }

    private byte getUKD() {
        return (byte) (kdp.getSelectionModel().getSelectedIndex() | ykd.getSelectionModel().getSelectedIndex() << 2 |
                ob1.getSelectionModel().getSelectedIndex() << 4);
    }
}
