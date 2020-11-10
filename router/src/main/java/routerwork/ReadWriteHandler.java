package main.java.routerwork;

import java.io.*;

import java.nio.channels.*;
import java.nio.charset.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    
    private String SOH;
    public ReadWriteHandler()
    {
      SOH = "" + (char)1;
    }
    @Override
    public void completed(Integer result, Attachment attachment) {
        
      if (result == -1) {
        try {
          attachment.client.close();
          Router.removeClient(attachment.clientId);
          String port = attachment.server.getLocalAddress().toString().split(":")[1];
          System.out.format("[" + getServerName(port) + "]Stopped   listening to the   client %s%n",
              attachment.clientAddr);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        return;
      }
  
      if (attachment.isRead) {
        attachment.buffer.flip();
        int limits = attachment.buffer.limit();
        byte bytes[] = new byte[limits];
        attachment.buffer.get(bytes, 0, limits);
        Charset charset = Charset.forName("UTF-8");
        String message = new String(bytes, charset);
        String datum[] = message.split(SOH);
        attachment.message = datum;
        try
        {
            String port = attachment.server.getLocalAddress().toString().split(":")[1];
            System.out.format("["+ getServerName(port) +"]Client %s  says: %s%n", attachment.clientAddr,
            message.replace((char)1, '|'));
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        attachment.isRead = false; //write
        attachment.buffer.rewind();
        attachment.buffer.clear();
        byte[] data = message.getBytes(charset);
        attachment.buffer.put(data);
        attachment.buffer.flip();
        if (attachment.client.isOpen() && Router.getSize() > 1)
        {
            new CheckSum().performAction(attachment, IResponsibility.CHECKSUM);
        }

  
      } else {
        // Write to client
        attachment.isRead = true;
        attachment.buffer.clear();
        attachment.client.read(attachment.buffer, attachment, this);

      }
    }
    @Override
    public void failed(Throwable e, Attachment attachment) {
      e.printStackTrace();
    }
    private String getServerName(String port)
    {
        if (port.equals("5000"))
            return "Broker Server";
        else if(port.equals("5001"))
            return "Market Server";
        else
          return null;
    }
  }