package main.java.brokerwork;

import java.nio.charset.*;
import java.nio.channels.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    @Override
    public void completed(Integer result, Attachment attachment) {
      if (result == -1)
        {
          attachment.mainThread.interrupt();
          System.out.println("Server has been interrupted\n");
          System.out.println("Shutting down...");
          return ;
        }
      if (attachment.isRead) {
        attachment.buffer.flip();
        Charset charset = Charset.forName("UTF-8");
        int limits = attachment.buffer.limit();
        byte bytes[] = new byte[limits];
        attachment.buffer.get(bytes, 0, limits);
        String message = new String(bytes, charset);
        if (attachment.clientId == 0)
        {
          attachment.clientId = Integer.parseInt(message);
          System.out.println("Broker (unique) ID: " + attachment.clientId);
        }
        else
          System.out.println("Server Message Response: "+ message.replace((char)1, '|'));
        try {
          boolean s = Broker.proccessReply(message);
          if (s == true && Broker.bs == 1)
            Broker.updateData(true);
          if (s == true && Broker.bs == 0)
            Broker.updateData(false);
        } catch (Exception e) {
          e.printStackTrace();
        }
        attachment.buffer.clear();
        message = testMe(attachment);
        if (message.contains("bye") || i > 3) {
          attachment.mainThread.interrupt();
          return;
        }
        i++;
        System.out.println("\nBroker response:" + message.replace((char)1, '|'));
        byte[] data = message.getBytes(charset);
        attachment.buffer.put(data);
        attachment.buffer.flip();
        attachment.isRead = false; //write
        attachment.client.write(attachment.buffer, attachment, this);
      }else {
        attachment.isRead = true;
        attachment.buffer.clear();
        attachment.client.read(attachment.buffer, attachment, this);
      }
    }
    @Override
    public void failed(Throwable e, Attachment attachment) {
      e.printStackTrace();
    }
    private String testMe(Attachment attachment)
    {
      String message;
      
      if (Broker.bs == 1)
        message = Broker.buyProduct(Broker.destinationId);
      else
        message = Broker.sellProduct(Broker.destinationId);
      return message + getCheckSum(message);
    }
    private String getCheckSum(String message)
    {
        int j = 0;
        char t[];
        String soh = "" + (char)1;
        String datum[] = message.split(soh);
        for(int k = 0; k < datum.length; k++)
        {
          t = datum[k].toCharArray();
          for(int i = 0; i < t.length; i++)
          {
            j += (int)t[i];
          }
          j += 1;
        }
        return ("10="+ (j % 256) + soh);
    }
    private static int i = 0;
  }