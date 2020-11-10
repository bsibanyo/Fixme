package main.java.routerwork;

public class EchoBack implements IResponsibility
{
    private int ECHOBACK = IResponsibility.ECHOBACK;
    @Override
    public void performAction(Attachment attachment, int response)
    {
        if (response != ECHOBACK)
            return ;
        attachment.isRead = false;
        attachment.client.write(attachment.buffer, attachment, attachment.rwHandler);
    }
}