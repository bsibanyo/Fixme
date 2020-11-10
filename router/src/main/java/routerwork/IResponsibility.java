package main.java.routerwork;


public interface IResponsibility
{
     int CHECKSUM = 1;
     int DISPATCH = 2;
     int ECHOBACK = 3;
     void performAction(Attachment attachment, int response);
}