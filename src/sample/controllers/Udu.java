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

public class Udu extends Upt implements Initializable, Observer, EventHandler<WindowEvent> {



    public ChoiceBox ru_open;
    public ChoiceBox ru_is_close;
    public ChoiceBox ru_is_open;
    public ChoiceBox ru_close;
    public CheckBox udu_is_blocked;
    public ChoiceBox sdu_udu;

    private byte number;

    public Udu() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] s = {"Норма", "Пуск", "КЗ", "Обрыв"};

        ru_open.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ru_is_close.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ru_is_open.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        ru_close.setItems(FXCollections.observableArrayList(Arrays.asList(s)));
        sdu_udu.setItems(FXCollections.observableArrayList(Arrays.asList(s)));

        ru_open.getSelectionModel().select(0);
        ru_is_close.getSelectionModel().select(0);
        ru_close.getSelectionModel().select(0);
        ru_is_open.getSelectionModel().select(0);
        sdu_udu.getSelectionModel().select(0);
        System.out.println("УПC №" + number + " init");
        Controller.uptList.add(this);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {

            if ((getSelectedIndex(ru_open) > 1 && getSelectedIndex(ru_is_close) < 3) ||
                    (getSelectedIndex(ru_is_open) > 1 && getSelectedIndex(ru_close) < 3)) {
                error.setSelected(true);
            } else {
                error.setSelected(false);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private int getSelectedIndex(ChoiceBox choiceBox) {
        return choiceBox.getSelectionModel().getSelectedIndex();
    }

    public byte[] getState() {
        byte[] state = new byte[]{1, 4, 4,
                getByteState_udu(),
                getRu(),
                getSdu(),
                (byte) (Double.parseDouble(u_power.getText()) * 10 - 100)
        };
        return state;
    }

    private byte getByteState_udu() {
        byte b = 0x03;
        if (error.isSelected())
            b |= 0x20;
        if (error_power.isSelected())
            b |= 0x40;
        if (udu_is_blocked.isSelected())
            b |= 0x80;

        return b;
    }

    private byte getRu() {
        return (byte) (ru_open.getSelectionModel().getSelectedIndex() | ru_close.getSelectionModel().getSelectedIndex() << 2 |
                ru_is_open.getSelectionModel().getSelectedIndex() << 4 | ru_is_close.getSelectionModel().getSelectedIndex() << 6);
    }

    private byte getSdu() {
        return (byte) (sdu_udu.getSelectionModel().getSelectedIndex());
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

                    }
                } else if (input.get(1) == 0x04) {
                    ((SerialPortUpt) o).response(getState());
                }
                if (input.get(1) == 0x11) {
                    System.out.println("SEARCH" + number);
                    ((SerialPortUpt) o).response(new byte[]{number, 0x11, 0x01, 0x03});
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
