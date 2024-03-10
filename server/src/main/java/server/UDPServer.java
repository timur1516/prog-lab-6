package server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
    private DatagramSocket ds;

    private int clientPort = -1;
    private InetAddress clientHost = null;

    UDPServer(int serverPort) throws SocketException {
        ds = new DatagramSocket(serverPort);
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

        this.clientHost = dp.getAddress();
        this.clientPort = dp.getPort();

        return dp.getData();
    }

    private void send(byte[] arr) throws IOException {
        if(this.clientPort == -1 && this.clientHost == null) throw new RuntimeException("Unknown client!");
        DatagramPacket dp = new DatagramPacket(arr, arr.length, this.clientHost, this.clientPort);
        ds.send(dp);
    }

    public Serializable receiveObject() throws IOException, ClassNotFoundException {
        Integer dataLen = (Integer) deserialize(receive(81));
        return deserialize(receive(dataLen));
    }

    public void sendObject(Serializable o) throws IOException {
        byte arr[] = serialize(o);
        send(serialize(arr.length));
        send(arr);
    }
}
