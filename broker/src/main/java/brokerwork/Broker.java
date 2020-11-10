package main.java.brokerwork;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class Broker {
    private static int quantity = 100;
    private static int cash = 1000;
    private static Attachment attachment;
    private static final String fixv = "8=FIX.4.2";
    public static int bs;
    public static int destinationId;

    public Broker(int id, int by) {
        destinationId = id;
        bs = by;
    }

    public void contact() throws Exception
    {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Broker is now connected!");
        attachment = new Attachment();
        attachment.client = channel;
        attachment.buffer = ByteBuffer.allocate(2048);
        attachment.isRead = true;
        
        attachment.mainThread = Thread.currentThread();

        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attachment.buffer, attachment, readWriteHandler);
        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
        }
    }
    public static String sellProduct(int destination) {
        String soh = "" + (char)1;
        // From 
        // To
        String message = " Selling Accepted: id="+attachment.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
        message += "49="+attachment.clientId+soh;

        String errorMessage = "Buying Rejected, Insuffient funds!";
        if (quantity > 0)
            return message;
        else
            return errorMessage;
    }

    public static String buyProduct(int destination) {
        String soh = "" + (char)1;
        String message = " Selling Accepted: id="+attachment.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
        message += "49="+attachment.clientId+soh;

        String errorMessage = "Buying Rejected, Insuffient funds!";
        if (cash > 0)
            return message;
        else
            return errorMessage;
    }

    public static boolean proccessReply(String reply) {
        String data[] = reply.split(""+(char)1);
        String tag = "";
        String state = "";

        for (String data_ : data) {
            if (data_.contains("35="))
                tag = data_.split("=")[1];
            if (data_.contains("49="))
                state = data_.split("=")[1];
        }

        if (tag.equals("8") && state.equals("8")) {
            System.out.println("\nMarket[" + destinationId +"] Order Rejected!\n");
            return false;
        }

        if (tag.equals("8") && state.equals("2")) {
            System.out.println("\nMarket[" + destinationId +"] Order Accepted!\n");
            return true;
        }
        return false;
    }

    public static void updateData(boolean state) {
        if (state == false) {
            quantity -= 2;
            cash += 55;
        } else {
            quantity += 2;
            cash -= 90;
        }   
    }
}