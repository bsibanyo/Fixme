package main.java.routerwork;

import java.nio.channels.*;
import java.net.*;

public class StartServer implements Runnable
{
    private String _host;
	private int _port;
	public StartServer(String host, int port) {
        _host = host;
        _port = port;
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
            InetSocketAddress sAddr = new InetSocketAddress(_host, _port);
            server.bind(sAddr);
            if (_port % 2 == 0)
                System.out.format("Broker Server: %s%n", sAddr);
            else
                System.out.format("Market Server: %s%n", sAddr);
            Attachment attachment = new Attachment();
            attachment.server = server;
            server.accept(attachment, new ConnectionHandler());
            Thread.currentThread().join();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}