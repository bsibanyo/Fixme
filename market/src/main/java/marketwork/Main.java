package main.java.marketwork;

public class Main
{
  public static void main(String[] args) {
    if (args.length != 0) {
        Market market = new Market(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    try {
      market.contact();
    }
    catch(Exception e) {
      System.out.println(e);
    }
  } else {
      System.out.println( "ERROR!\n" + "Please fill in the Quantity & Price of your product.");
      System.out.println( "e.g Quantity=100 Price=15\n");
      System.out.println( "Example:\njava -jar market-1.0.jar 100 15\n");
    }
  }
}