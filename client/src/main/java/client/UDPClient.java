package client;

import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class UDPClient {
    DatagramSocket ds;
    InetAddress host;
    int port;

    UDPClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;

    }

    public void open() throws SocketException {
        this.ds = new DatagramSocket();
        this.ds.setSoTimeout(1000);
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
        this.ds.receive(dp);
        return arr;
    }

    private void send(byte[] arr) throws IOException {
        DatagramPacket dp = new DatagramPacket(arr, arr.length, this.host, this.port);
        this.ds.send(dp);
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
