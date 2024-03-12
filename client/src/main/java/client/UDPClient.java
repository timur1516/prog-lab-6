package client;

import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPClient {
    DatagramChannel dc;
    InetSocketAddress addr;

    UDPClient(InetAddress host, int port) throws IOException {
        this.addr = new InetSocketAddress(host, port);
        dc = DatagramChannel.open();
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
        ByteBuffer buf = ByteBuffer.wrap(arr);
        dc.receive(buf);
        return buf.array();
    }

    private void send(byte[] arr) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(arr);
        dc.send(buf, addr);
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
