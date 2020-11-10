package main.java.routerwork;

public class EchoBack implements IResponsibility
{
    private int ECHOBACK = IResponsibility.ECHOBACK;
    @Override
    public void performAction(Attachment attach, int resp)
    {
        if (resp != ECHOBACK)
            return ;
        attach.isRead = false;
        attach.client.write(attach.buffer, attach, attach.rwHandler);
    }
}