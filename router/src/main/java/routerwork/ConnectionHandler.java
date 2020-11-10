package main.java.routerwork;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;

import java.io.*;
import java.nio.charset.*;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {
      private static int clientId = 777;
  @Override
  public void completed(AsynchronousSocketChannel client, Attachment attachment) {
    try {
      SocketAddress clientAddr = client.getRemoteAddress();
      System.out.format("Connection accepted %s%n", clientAddr);
      attachment.server.accept(attachment, this);
      ReadWriteHandler rwHandler = new ReadWriteHandler();
      Attachment newAttachment = new Attachment();
      newAttachment.server = attachment.server;
      newAttachment.client = client;
      newAttachment.clientId = clientId++;
      newAttachment.buffer = ByteBuffer.allocate(2048);
      newAttachment.isRead = false;
      newAttachment.clientAddr = clientAddr;
      Charset charset = Charset.forName("UTF-8");
      byte data [] = Integer.toString(newAttachment.clientId).getBytes(charset);
      newAttachment.rwHandler = rwHandler;
      newAttachment.buffer.put(data);
      newAttachment.buffer.flip();
      Router.addClient(newAttachment);
      client.write(newAttachment.buffer, newAttachment, rwHandler);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void failed(Throwable e, Attachment attachment) {
    System.out.println("Connection Failed.");
    e.printStackTrace();
  }
}
