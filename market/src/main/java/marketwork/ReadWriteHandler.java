package main.java.marketwork;

import java.nio.charset.*;
import java.nio.channels.*;
import java.io.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    @Override
    public void completed(Integer result, Attachment attachment) {
      if (result == -1) {

        attachment.mainThread.interrupt();
        System.out.println("Server is shutting down");
        System.out.println("Market is going OFFLINE...");
        return ;
      }

      if (attachment.isRead) {
        attachment.buffer.flip();
        Charset charset = Charset.forName("UTF-8");
        int limits = attachment.buffer.limit();
        byte bytes[] = new byte[limits];
        attachment.buffer.get(bytes, 0, limits);
        String message = new String(bytes, charset);
        if (attachment.clientId == 0) {
          attachment.clientId = Integer.parseInt(message);
          System.out.println("Market (unique) ID: " + attachment.clientId);
          attachment.isRead = false;
          attachment.client.read(attachment.buffer, attachment, this);
          return ;
        } else
            System.out.println("Server Responded: "+ message.replace((char)1, '|'));

        attachment.buffer.clear();
        message = Market.processRequest(message);

        if (message.contains("bye")) {
            attachment.mainThread.interrupt();
            return;
        }

        try {
          System.out.println("\nMarket Response: "+ message.replace((char)1, '|'));
        } catch (Exception e) {
         
        }

        byte[] data = message.getBytes(charset);
        attachment.buffer.put(data);
        attachment.buffer.flip();
        attachment.isRead = false; //write
        attachment.client.write(attachment.buffer, attachment, this);
      } else {
        attachment.isRead = true;
        attachment.buffer.clear();
        attachment.client.read(attachment.buffer, attachment, this);
      }
    }

    @Override
    public void failed(Throwable e, Attachment attachment) {
      e.printStackTrace();
    }

    private String getTextFromUser() throws Exception {
      System.out.print("Please enter a  message  (Bye  to quit):");
      BufferedReader consoleReader = new BufferedReader(
          new InputStreamReader(System.in));
      String message = consoleReader.readLine();
      return message;
    }

    private String testMe() {
      String soh = "" + (char)1;
      String message = "id="+777+soh+"56="+778+soh+"message=from market"+soh;
      return message + getCheckSum(message);
    }

    private String getCheckSum(String message) {

        int a = 0;
        char c[];

        String soh = "" + (char)1;
        String datum[] = message.split(soh);
        for(int b = 0; b < datum.length; b++) {
          c = datum[b].toCharArray();
          for(int d = 0; d < c.length; d++) {
            a += (int)c[d];
          }
          a += 1;
        }
        return ("10="+ (a % 256) + soh);
    }
    private static int i = 0;
  }