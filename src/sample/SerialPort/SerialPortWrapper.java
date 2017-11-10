package sample.SerialPort;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.List;


public class SerialPortWrapper extends SerialPort {
    private static SerialPort serialPort;

    public SerialPortWrapper(SerialPort serialPort) {
        super(serialPort.getPortName());
        SerialPortWrapper.serialPort = serialPort;
    }

    public static List<String> getPortNames() throws SerialPortException {
        StringBuilder name;
        List<String> stringPorts = new ArrayList<String>();
        for (Integer i = 1; i < 40; i++) {
            name = new StringBuilder();
            name.append("COM").append(i.toString());
            serialPort = new SerialPort(name.toString());
            try {
                serialPort.openPort();
                stringPorts.add(name.toString());
            } catch (SerialPortException ignored) {
            } finally {
                if (serialPort.isOpened())
                    serialPort.closePort();
            }
        }
        return stringPorts;
    }
}
