package sample.controllers;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.SerialPort.SerialPortUpt;

import java.net.URL;
import java.util.*;

public class Ups extends Upt implements Initializable, Observer, EventHandler<WindowEvent> {

    public ChoiceBox<String> sh2;
    public ChoiceBox<String> sh1;
    public ChoiceBox<String> sh3;
    public ChoiceBox<String> sh4;
    public CheckBox ups_is_blocked;
    public CheckBox ups_fire1;
    public CheckBox ups_fire2;

    private byte number;
    private byte[] state;
    private static boolean resetSh1;
    private static boolean resetSh2;
    private static boolean resetSh3;
    private static boolean resetSh4;


    public Ups() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] sSh = {"Норма", "Норма", "КЗ", "Обрыв", "Пожар1", "Пожар2"};

        sh1.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sh2.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sh3.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));
        sh4.setItems(FXCollections.observableArrayList(Arrays.asList(sSh)));

        resetSh1 = false;
        resetSh2 = false;
        resetSh3 = false;
        resetSh4 = false;

        sh1.getSelectionModel().select(0);
        sh2.getSelectionModel().select(0);
        sh3.getSelectionModel().select(0);
        sh4.getSelectionModel().select(0);


        System.out.println("УПC №" + number + " init");
        Controller.uptList.add(this);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {

            if ((getSelectedIndex(sh1) > 1 && getSelectedIndex(sh1) < 4) ||
                    (getSelectedIndex(sh2) > 1 && getSelectedIndex(sh2) < 4) ||
                    (getSelectedIndex(sh3) > 1 && getSelectedIndex(sh3) < 4) ||
                    (getSelectedIndex(sh4) > 1 && getSelectedIndex(sh4) < 4)) {
                error.setSelected(true);
            } else {
                error.setSelected(false);
            }
            if (getSelectedIndex(sh1) > 3 || getSelectedIndex(sh2) > 3 ||
                    getSelectedIndex(sh3) > 3 || getSelectedIndex(sh4) > 3) {
                ups_fire1.setSelected(true);
            } else {
                ups_fire1.setSelected(false);
            }
            if (getSelectedIndex(sh1) > 4 || getSelectedIndex(sh2) > 4 ||
                    getSelectedIndex(sh3) > 4 || getSelectedIndex(sh4) > 4) {
                ups_fire2.setSelected(true);
            } else {
                ups_fire2.setSelected(false);
            }

            if (resetSh1) {
                sh1.getSelectionModel().select(0);
                resetSh1 = false;
            }
            if (resetSh2) {
                sh2.getSelectionModel().select(0);
                resetSh2 = false;
            }
            if (resetSh3) {
                sh3.getSelectionModel().select(0);
                resetSh3 = false;
            }
            if (resetSh4) {
                sh4.getSelectionModel().select(0);
                resetSh4 = false;
            }


        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private int getSelectedIndex(ChoiceBox choiceBox) {
        return choiceBox.getSelectionModel().getSelectedIndex();
    }

    public byte[] getState() {
        state = new byte[]{1, 4, 4,
                getByteState_ups(),
                getState_sh1_sh2(),
                getState_sh3_sh4(),
                (byte) (Double.parseDouble(u_power.getText()) * 10 - 100)
        };
        return state;
    }

    private byte getByteState_ups() {
        byte b = 0x02;
        if (error.isSelected())
            b |= 0x08;
        if (error_power.isSelected())
            b |= 0x10;
        if (ups_is_blocked.isSelected())
            b |= 0x20;
        if (ups_fire1.isSelected())
            b |= 0x40;
        if (ups_fire2.isSelected())
            b |= 0x80;
        return b;
    }

    private byte getState_sh1_sh2() {
        return (byte) (sh1.getSelectionModel().getSelectedIndex() | sh2.getSelectionModel().getSelectedIndex() << 3);
    }

    private byte getState_sh3_sh4() {
        return (byte) (sh3.getSelectionModel().getSelectedIndex() | sh4.getSelectionModel().getSelectedIndex() << 3);
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
//                System.out.println("is correct");
                if (input.get(1) == 0x06) {
                    switch (input.get(2)) {
                        case 0x01:
                            System.out.println("RESET");
                            resetSh1 = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x01});
                            break;
                        case 0x02:
                            System.out.println("AUTO IS ON");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x02});

                            break;
                        case 0x03:
                            System.out.println("STARTUP");
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x03});
                            break;
                        case 0x04:
                            break;
                        case 0x11:
                            System.err.println("RESET SH1 on device #" + number);
                            resetSh1 = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x11});
                            break;
                        case 0x12:
                            System.err.println("RESET SH2");
                            resetSh2 = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x12});
                            break;
                        case 0x13:
                            System.err.println("RESET SH3");
                            resetSh3 = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x13});
                            break;
                        case 0x14:
                            System.err.println("RESET SH4");
                            resetSh4 = true;
                            ((SerialPortUpt) o).response(new byte[]{number, 0x06, 0x14});
                            break;

                    }
                } else if (input.get(1) == 0x04) {
                    ((SerialPortUpt) o).response(getState());
                }
                if (input.get(1) == 0x11) {
                    System.out.println("SEARCH" + number);
                    ((SerialPortUpt) o).response(new byte[]{number, 0x11, 0x01, 0x02});
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
}
