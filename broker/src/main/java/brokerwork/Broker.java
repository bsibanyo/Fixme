package main.java.brokerwork;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class Broker {
    private static int qty = 100;
    private static int cash = 1000;
    private static Attachment attach;
    private static final String fixv = "8=FIX.4.2";
    public static int bs;
    public static int dstId;

    public Broker(int id, int by) {
        dstId = id;
        bs = by;
    }

    public void contact() throws Exception
    {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Broker is now connected!");
        attach = new Attachment();
        attach.client = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        
        attach.mainThread = Thread.currentThread();

        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attach.buffer, attach, readWriteHandler);
        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
          //  e.printStackTrace();
        }
    }
    public static String sellProduct(int dst) {
        String soh = "" + (char)1;
        // From 
        // To
        String msg = " Selling Accepted: id="+attach.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
        msg += "49="+attach.clientId+soh;

        String errMsg = "Buying Rejected, Insuffient funds!";
        if (qty > 0)
            return msg;
        else
            return errMsg;
    }

    public static String buyProduct(int dst) {
        String soh = "" + (char)1;
        String msg = " Selling Accepted: id="+attach.clientId+soh+fixv+soh+"35=D"+soh+"49=CLIENT"+soh+"56=SERVER"+soh+"11=ID"+soh+"55=USD/ZAR"+soh;
        msg += "49="+attach.clientId+soh;

        String errMsg = "Buying Rejected, Insuffient funds!";
        if (cash > 0)
            return msg;
        else
            return errMsg;
    }

    public static boolean proccessReply(String reply) {
        String data[] = reply.split(""+(char)1);
        String tag = "";
        String state = "";

        for (String dat : data) {
            if (dat.contains("35="))
                tag = dat.split("=")[1];
            if (dat.contains("49="))
                state = dat.split("=")[1];
        }

        if (tag.equals("8") && state.equals("8")) {
            System.out.println("\nMarket[" + dstId +"] Order Rejected!\n");
            return false;
        }

        if (tag.equals("8") && state.equals("2")) {
            System.out.println("\nMarket[" + dstId +"] Order Accepted!\n");
            return true;
        }
        return false;
    }

    public static void updateData(boolean state) {
        if (state == false) {
            qty -= 2;
            cash += 55;
        } else {
            qty += 2;
            cash -= 90;
        }   
    }
}