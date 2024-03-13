package server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UDPServer {
    DatagramChannel dc;
    SocketAddress addr;

    UDPServer(int serverPort) {
        addr = new InetSocketAddress(serverPort);

    }

    public void open() throws IOException {
        this.dc = DatagramChannel.open();
        this.dc.bind(addr);
        this.dc.configureBlocking(false);
    }

    public void registerSelector(Selector selector, int ops) throws ClosedChannelException {
        this.dc.register(selector, ops);
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
        ByteBuffer buf = ByteBuffer.allocate(len);
        addr = this.dc.receive(buf);
        return buf.array();
    }

    private void send(byte[] arr) throws IOException {
         ByteBuffer buf = ByteBuffer.wrap(arr);
         this.dc.send(buf, addr);
    }

    public Serializable receiveObject() throws IOException, ClassNotFoundException {
        byte dataLenArr[] = receive(81);
        Integer dataLen = (Integer) deserialize(dataLenArr);
        return deserialize(receive(dataLen));
    }

    public void sendObject(Serializable o) throws IOException {
        byte arr[] = serialize(o);
        send(serialize(arr.length));
        send(arr);
    }
}