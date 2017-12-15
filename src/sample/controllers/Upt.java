package sample.controllers;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.SerialPort.SerialPortUpt;

import java.net.URL;
import java.util.*;

public class Upt implements Initializable, Observer, EventHandler<WindowEvent> {
    public CheckBox error_power;
    public CheckBox error_akb;
    public CheckBox upt_is_blocked;
    public CheckBox upt_fire1;
    public CheckBox upt_fire2;
    public CheckBox error;
    public CheckBox avt_off;
    public ChoiceBox<String> P1;
    public ChoiceBox<String> sh2;
    public ChoiceBox<String> sh1;
    public ChoiceBox<String> sh3;
    public ChoiceBox<String> vu;
    public ChoiceBox<String> sdu;
    public ChoiceBox<String> kva;
    public ChoiceBox<String> gu;
    public ChoiceBox<String> gn;
    public ChoiceBox<String> ao;
    public ChoiceBox<String> ob1;
    public ChoiceBox<String> ykd;
    public ChoiceBox<String> kdp;
    public TextField сondown;
    public TextField u_power;
    public TextField u_power_akb;
    public CheckBox stop_startup;
    public Button buttonReset;
    public CheckBox otv_derived;
    public CheckBox fire_from2;
    public CheckBox fire_from1;
    public CheckBox startup_is_goin;
    public CheckBox door_is_open;
    private byte number;
    private byte[] state;
    private boolean reset;

    private int startupCondown;

    public Upt() {
    }

    int counterCondown;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] s = {"Норма", "Пуск", "КЗ", "Обрыв"};
        String[] sSh = {"Норма", "Норма", "КЗ", "Обрыв", "Пожар1", "Пожар2"};
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
        System.out.println("УПТ №" + number + " init");
        Controller.uptList.add(this);
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
        reset = false;
        counterCondown = 10;
        startupCondown = 0;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {



            if (getSelectedIndex(P1) > 1 || getSelectedIndex(P1) > 1 || (getSelectedIndex(sh1) > 1 && getSelectedIndex(sh1) < 4) ||
                    (getSelectedIndex(sh2) > 1 && getSelectedIndex(sh2) < 4) || (getSelectedIndex(sh3) > 1 && getSelectedIndex(sh3) < 4) ||
                    getSelectedIndex(sdu) > 1 || getSelectedIndex(vu) > 1 || getSelectedIndex(kva) > 1 || getSelectedIndex(ykd) > 1
                    || getSelectedIndex(kdp) > 1 || getSelectedIndex(ob1) > 1 || getSelectedIndex(ao) > 1 || getSelectedIndex(gn) > 1 || getSelectedIndex(gu) > 1) {
                error.setSelected(true);
                if (!avt_off.isSelected())
                    avt_off.setSelected(true);
            } else {
                error.setSelected(false);
            }
            if (!upt_fire2.isSelected()) {
                if ((getSelectedIndex(sh1) == 4 && getSelectedIndex(sh2) != 4 && getSelectedIndex(sh3) != 4) ||
                        (getSelectedIndex(sh1) != 4 && getSelectedIndex(sh2) == 4 && getSelectedIndex(sh3) != 4) ||
                        (getSelectedIndex(sh1) != 4 && getSelectedIndex(sh2) != 4 && getSelectedIndex(sh3) == 4)) {
                    upt_fire1.setSelected(true);
                } else {
                    upt_fire1.setSelected(false);
                }

                if ((getSelectedIndex(sh1) == 4 && getSelectedIndex(sh2) == 4) || (getSelectedIndex(sh1) == 4 && getSelectedIndex(sh3) == 4) ||
                        (getSelectedIndex(sh2) == 4 && getSelectedIndex(sh3) == 4) || getSelectedIndex(sh1) == 5 || getSelectedIndex(sh2) == 5 ||
                        getSelectedIndex(sh3) == 5) {
                    upt_fire2.setSelected(true);
                    //========================================================//
                    //FIRE2 firefigtheng go/////////////////////////////////////
                    //********************************************************//
                    fire_from2.setSelected(true);
                    fire_from1.setSelected(true);

                    sh1.setDisable(true);
                    sh2.setDisable(true);
                    sh3.setDisable(true);
                    сondown.setText(String.format("%d", counterCondown));

                } else {
                    upt_fire2.setSelected(false);
                }
            }
            if (getSelectedIndex(kdp) == 1) {
                avt_off.setSelected(true);
                upt_fire2.setSelected(true);
                fire_from2.setSelected(true);
                fire_from1.setSelected(false);
            }
            if (getSelectedIndex(kva) == 1) {
                avt_off.setSelected(false);
            }

            if (getSelectedIndex(ykd) == 1) {
                avt_off.setSelected(true);
            }
            if (getSelectedIndex(sdu) == 1) {
                otv_derived.setSelected(true);
            }
            if (reset) {
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
                sh1.setDisable(false);
                sh2.setDisable(false);
                sh3.setDisable(false);
                reset = false;
                counterCondown = 10;
                startupCondown = 0;
                otv_derived.setSelected(false);
                upt_fire2.setSelected(false);
                upt_fire1.setSelected(false);
                stop_startup.setSelected(false);
                startup_is_goin.setSelected(false);
                otv_derived.setSelected(false);
                fire_from2.setSelected(false);
                fire_from1.setSelected(false);
            }

            if (!upt_fire2.isSelected()) {
                stop_startup.setSelected(false);
            }
//fire algoritm//
            if (upt_fire2.isSelected()) {
                if (stop_startup.isSelected()) {
                    сondown.setText("0");
                    counterCondown = 10;
                } else {
                    сondown.setText(String.format("%d", counterCondown));
                    if (counterCondown > 0) {
                        counterCondown--;
                    } else {
                        if (startupCondown < 5) {
                            P1.getSelectionModel().select(1);
                        } else {
                            startup_is_goin.setSelected(true);
                            startupCondown = 5;
                            P1.getSelectionModel().select(0);
                            if (otv_derived.isSelected()) {
                                //ОТВ подано
                            } else {
                                //ОТВ не подано
                            }
                        }
                        startupCondown++;
                    }
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private int getSelectedIndex(ChoiceBox choiceBox) {
        return choiceBox.getSelectionModel().getSelectedIndex();
    }

    public byte[] getState() {
        state = new byte[]{1, 4, 10,
                getByteStateZero_upt(),
                getByteState_upt(),
                getState_sh1_sh2(),
                getState_sh3_P1(),
                getTablo(),
                getUKD(),
                Byte.parseByte(сondown.getText()),
                (byte) (Double.parseDouble(u_power.getText()) * 10 - 100),
                (byte) (Double.parseDouble(u_power_akb.getText()) * 10 - 100),
                0x00};
        return state;
    }

    private byte getByteStateZero_upt() {
        byte b = 0x01;
        //todo RIP state
        if (fire_from2.isSelected())
            b |= 0x04;
        if (fire_from1.isSelected())
            b |= 0x08;
        if (stop_startup.isSelected())
            b |= 0x10;
        if (otv_derived.isSelected())
            b |= 0x20;
        if (startup_is_goin.isSelected())
            b |= 0x40;

        return b;
    }


    private byte getByteState_upt() {
        byte b = 0x00;
        if (error.isSelected())
            b = 0x01;
        if (error_power.isSelected())
            b |= 0x02;
        if (error_akb.isSelected())
            b |= 0x04;
//        if (stop_startup.isSelected())//duplicated
//            b |= 0x08;
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

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg != null) {
            List<Byte> input = (List<Byte>) arg;
            System.out.println(input.toString());
            if (number == input.get(0)) {
                System.out.println("is correct");
                if (input.get(1) == 0x06) {
                    switch (input.get(2)) {
                        case 0x01:
                            System.out.println("RESET");
                            reset = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x01});
                            break;
                        case 0x02:
                            System.out.println("AUTO IS ON");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x02});
                            avt_off.setSelected(false);
                            break;
                        case 0x03:
                            System.out.println("STARTUP");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x03});
                            upt_fire2.setSelected(true);
                            stop_startup.setSelected(false);
                            avt_off.setSelected(true);
                            fire_from1.setSelected(true);
                            fire_from2.setSelected(true);
                            break;
                        case 0x04:
                            System.out.println("Look");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x04});
                            if (upt_is_blocked.isSelected())
                                upt_is_blocked.setSelected(false);
                            else
                                upt_is_blocked.setSelected(true);
                            break;
                        case 0x05:
                            System.out.println("test display");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x05});
                            break;
                        case 0x21:
                            System.out.println("Start STARTUP no timeout");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x21});
                            if (upt_fire2.isSelected()) {
                                stop_startup.setSelected(false);
                                counterCondown = 0;
                            }
                            break;
                        case 0x20:
                            System.out.println("STOP STARTUP ");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x20});
                            if (upt_fire2.isSelected()) {
                                stop_startup.setSelected(true);
                            }
                            break;
                        default:
                            System.out.println("RESET");
                            reset = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x01});
                            break;

                    }
                } else if (input.get(1) == 0x04) {
                    ((SerialPortUpt) o).response(getState());
                }
                if (input.get(1) == 0x11) {
                    System.out.println("SEARCH" + number);
                    ((SerialPortUpt) o).response(new byte[]{number, 0x11, 0x01, 0x01});
                } else {
                    System.out.println("Something als");
                }
            }
        }
    }


    //stop timer/
    @Override
    public void handle(WindowEvent event) {

    }

    public void resetDevice(ActionEvent actionEvent) {
        reset = true;
    }
}
