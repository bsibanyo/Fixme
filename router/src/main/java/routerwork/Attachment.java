package main.java.routerwork;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;

public class Attachment {
    public AsynchronousServerSocketChannel server;
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public SocketAddress clientAddr;
    public String message[];
    public ReadWriteHandler rwHandler;
    public boolean isRead;
}
  