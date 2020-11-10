package main.java.marketwork;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;
import java.util.concurrent.Future;
public class Market
{
    private static int quantity;
    private static int price;
    private static int request;
    private static int destinationId;
    private static final String fixv = "8=FIX.4.2";
    private static Attachment attachment;

    public Market(int quantity_, int price_)
    {
        quantity = quantity_;
        price = price_;
        try {
            Random rand = new Random();
            request = rand.nextInt(3) + 1;
        }
        catch(Exception e) {
            request = 2;
        }
    }

    public void contact() throws Exception {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Market is now connected!");
        attachment = new Attachment();
        attachment.client = channel;
        attachment.buffer = ByteBuffer.allocate(2048);
        attachment.isRead = true;
        attachment.mainThread = Thread.currentThread();


        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attachment.buffer, attachment, readWriteHandler);
        try {
            Thread.currentThread().join();
        } catch(Exception e) {
            
        }
    }
    public static String processRequest(String response)
    {
        String data[] = response.split("" + (char)1);
        String messageType="";
        String requestType="";
        String price="";
        String _quantity="";
        for(String data_ : data)
        {
            if (data_.contains("35="))
                messageType = data_.split("=")[1];
            else if (data_.contains("49="))
                requestType = data_.split("=")[1];
            else if (data_.contains("56="))
                price = data_.split("=")[1];
            else if (data_.contains("11="))
                _quantity = data_.split("=")[1];
            else if (data_.contains("id="))
                destinationId = Integer.parseInt(data_.split("=")[1]);
        }
        
        return process(messageType, requestType, price, _quantity);
    }
    private static String process(String messageType, String requestType, String _price, String _quantity)
    {
        int p = Integer.parseInt(_price);
        int q = Integer.parseInt(_quantity);
        // /BROKER
        if (messageType.equals("D") && requestType.equals("2") && p < price && (request == 2 || request == 3))
            return getMessage(3, Integer.parseInt(_quantity)); //BUY 
        else if (messageType.equals("D") && requestType.equals("1") && p >= price && quantity - q >= 0 && (request == 2 || request == 3))
            return getMessage(2, Integer.parseInt(_quantity)); //SELL
        else
            return getMessage(1, Integer.parseInt(_quantity)); //REJECT
    }
    private static String getMessage(int code, int _quantity)
    {
        String soh = "" + (char)1;
        String message = "";
        if (code == 1)
            message = "id="+attachment.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
        if (code == 2)
        {
            message = "id="+attachment.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
            quantity -= _quantity;
        }
        if (code == 3)
        {
            message = "id="+attachment.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
            quantity += _quantity;
        }
        return message + getCheckSum(message);
    }
    private static String getCheckSum(String message)
    {
        int a = 0;
        char c[];
        String soh = "" + (char)1;
        String datum[] = message.split(soh);
        for(int b = 0; b < datum.length; b++)
        {
          c = datum[b].toCharArray();
          for(int d = 0; d < c.length; d++)
          {
            a += (int)c[d];
          }
          a += 1;
        }
        return ("10="+ (a % 256) + soh);
    }
}