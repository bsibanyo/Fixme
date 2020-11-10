package main.java.brokerwork;

public class Main {
  public static void main(String[] args) throws Exception {
    if (args.length == 2) {
      if (args[1].equals("1") || args[1].equals("2")) {
        Broker broker = new Broker(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        try {
          broker.contact();
        } catch (Exception e) {
          System.out.println(e);
        }
      } else {
        System.out.println("Buy/Sell [buy = 1 or sell = 2]");
      }

    } else {
      System.out.println("ERROR!\nPLEASE USE: java -jar broker-1.0.jar Market(unique)ID (e.g 55)");
      System.out.println("1 = Buy");
      System.out.println("2 = Sell");
      System.out.println( "Example:\njava -jar broker-1.0.jar 55 1 \n");
    }
  }
}