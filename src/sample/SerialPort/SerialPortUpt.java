package sample.SerialPort;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import sample.Main;
import sample.controllers.Controller;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class SerialPortUpt extends Observable {
    private static SerialPort serialPort;
    private volatile static SerialPortUpt portMigu;
    private byte myAddress;
    private byte[] outData;
    Controller controller;
    private static List<Observer> observers = new ArrayList<>();


    public void register(Observer observer) {
        observers.add(observer);
    }

    public void request(List<Byte> bytes) {
        for (Observer observer : observers) {
            observer.update(this, bytes);
        }
    }


    public SerialPortUpt() {
    }

    public SerialPortUpt getPort(String port, int speed, int bitData, String party, String stopBit, Observer observer, Controller controller) throws SerialPortException {
        this.controller = controller;
        if (portMigu == null) {
            synchronized (SerialPortUpt.class) {
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
//        System.out.println(Arrays.toString(outData));
    }

    public void response(byte[] out) {
        try {
            serialPort.writeBytes(new CRC16(out).getDataAndCrc());
            System.out.println("state" + Arrays.toString(out));

            TimeUnit.MILLISECONDS.sleep(12);

            setChanged();
            notifyObservers(out);
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SerialPortException {
        portMigu = null;
        if (serialPort != null)
            if (serialPort.isOpened())
                serialPort.closePort();
    }


    class SerialPortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(Main.isAlive) {
                if (event.getEventValue() > 3) {//Check bytes count in the input buffer
                    try {
                        ArrayList<Byte> bytes = new ArrayList<>();
                        bytes.addAll(arrayToList(serialPort.readBytes(2)));

                        switch (bytes.get(1)) {
                            case 4:
                                bytes.addAll(arrayToList(serialPort.readBytes(6)));
                                System.out.println(bytes.toString());
                                break;
                            case 6:
                                bytes.addAll(arrayToList(serialPort.readBytes(3)));
                                System.err.println(bytes.toString());
                                break;
                            case 17:
                                System.err.println("NAME Device");
                                byte buffer[] = serialPort.readBytes(2);
                                break;
                        }

                        request(bytes);

                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            }else {
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }

        private void returnName(byte address) {
            try {
                byte buffer[] = serialPort.readBytes(2);
                serialPort.writeBytes(new CRC16(new byte[]{address, 0x11, 0x01, 0x01}).getDataAndCrc());
                setChanged();
                notifyObservers(buffer);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

    }

    private List<Byte> arrayToList(byte[] bytes) {
        List<Byte> byteList = new ArrayList<>();
        for (byte aByte : bytes) byteList.add(aByte);

        return byteList;
    }
}
