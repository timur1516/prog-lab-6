package client;

import common.net.NetDataTransferringHandler;

import java.io.*;
import java.net.*;

public class UDPClient extends NetDataTransferringHandler {
    private static UDPClient UDP_CLIENT = null;
    DatagramSocket ds;
    InetAddress host;
    int port;
    int timeout;

    private UDPClient(){};
    public static UDPClient getInstance(){
        if(UDP_CLIENT == null){
            UDP_CLIENT = new UDPClient();
        }
        return UDP_CLIENT;
    }

    public void init(InetAddress host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void open() throws SocketException {
        this.ds = new DatagramSocket();
        this.ds.setSoTimeout(this.timeout);
    }

    @Override
    public void stop() {
        this.ds.close();
    }

    @Override
    protected byte[] receive(int len) throws IOException {
        byte arr[] = new byte[len];
        DatagramPacket dp = new DatagramPacket(arr, len);
        this.ds.receive(dp);
        return arr;
    }

    @Override
    protected void send(byte[] arr) throws IOException {
        DatagramPacket dp = new DatagramPacket(arr, arr.length, this.host, this.port);
        this.ds.send(dp);
    }
}
