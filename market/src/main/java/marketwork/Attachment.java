package main.java.marketwork;

import java.nio.channels.*;
import java.nio.*;

public class Attachment
{
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public Thread mainThread;
    public boolean isRead;
}
  