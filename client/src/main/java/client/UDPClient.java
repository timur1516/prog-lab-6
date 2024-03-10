package client;

import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;

import java.io.*;
import java.net.*;

public class UDPClient {
    private DatagramSocket ds;
    private InetAddress host;
    private int port;

    UDPClient(InetAddress host, int port) throws SocketException {
        this.host = host;
        this.port = port;
        ds = new DatagramSocket();
    }

    private byte[] serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.flush();
        return baos.toByteArray();
    }

    private Serializable deserialize(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Serializable) ois.readObject();
    }

    private byte[] receive(int len) throws IOException {
        byte arr[] = new byte[len];
        DatagramPacket dp = new DatagramPacket(arr, len);
        ds.receive(dp);
        return dp.getData();
    }

    private void send(byte[] arr) throws IOException {
        DatagramPacket dp = new DatagramPacket(arr, arr.length, this.host, this.port);
        ds.send(dp);
    }

    public Serializable receiveObject() throws ReceivingDataException {
        try {
            Integer dataLen = (Integer) deserialize(receive(81));
            return deserialize(receive(dataLen));
        }
        catch (Exception e){
            throw new ReceivingDataException("Error while receiving data from server!");
        }
    }

    public void sendObject(Serializable o) throws SendingDataException {
        try {
            byte arr[] = serialize(o);
            send(serialize(arr.length));
            send(arr);
        }
        catch (Exception e){
            throw new SendingDataException("Error while sending data to server!");
        }
    }

}
