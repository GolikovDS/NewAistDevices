package sample.SerialPort;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import sample.controllers.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;


public class SerialPortUpt extends Observable {
    private static SerialPort serialPort;
    private volatile static SerialPortUpt portMigu;
    private byte myAddress;
    private byte[] outData;
    Controller controller;

    public SerialPortUpt() {
    }

    public SerialPortUpt getPort(String port, int speed, int bitData, String party, String stopBit, Observer observer, byte myAddress, Controller controller) throws SerialPortException {
        this.controller = controller;
        if (portMigu == null) {
            synchronized (SerialPortUpt.class) {
                this.myAddress = myAddress;
                serialPort = new SerialPort(port);
                addObserver(observer);
                serialPort.openPort();
                serialPort.setParams(speed, bitData, Integer.parseInt(stopBit), SerialPort.PARITY_NONE);
                int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
                serialPort.setEventsMask(mask);
                serialPort.addEventListener(new SerialPortReader());
                portMigu = new SerialPortUpt();
            }
        }
        return portMigu;
    }


    public void responseToSerialPort(List<Byte> response) throws SerialPortException {
//        response.set(0, myAddress);
        CRC16 crc16 = new CRC16(response);
        outData = crc16.getDataAndCrc();
        System.out.println(Arrays.toString(outData));
    }

    public void closeConnection() throws SerialPortException {
        portMigu = null;
        if (serialPort != null)
            if (serialPort.isOpened())
                serialPort.closePort();
    }


    class SerialPortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventValue() > 3) {//Check bytes count in the input buffer
                try {
                    byte buff = serialPort.readBytes(1)[0];
                    String log = String.format("%d - address", buff);
                    System.out.println(log);
                    if (buff == myAddress) {
                        buff = serialPort.readBytes(1)[0];
                        log = String.format("%d - fun", buff);
                        System.out.println(log);
                        switch (buff) {
                            ////чтение имяни прибора УПТ - 0x01////
                            case 0x11:
                                returnName();
                                break;
                            case 0x07:
                                break;
                            case 0x03:
                                returnState();
                                break;
                            default:
                                returnName();
                                break;
                        }
                    }
                } catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }

        private void returnName() {
            try {
                byte buffer[] = serialPort.readBytes(2);
                serialPort.writeBytes(new CRC16(new byte[]{myAddress, 0x11, 0x01, 0x01}).getDataAndCrc());
                setChanged();
                notifyObservers(buffer);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

        private void returnState() {
            System.out.println("state" + Arrays.toString(outData));
            try {
                byte buffer[] = serialPort.readBytes(6);
                TimeUnit.MILLISECONDS.sleep(12);
//                serialPort.writeBytes(new byte[]{myAddress, 0x03, 0x08, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x02, 0x03});

//                serialPort.writeBytes(new CRC16(controller.getState()).getDataAndCrc());todo write hire
                setChanged();
                notifyObservers(buffer);
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
