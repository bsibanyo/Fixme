package main.java.routerwork;

public class ForwardTo implements IResponsibility
{
    private int DISPATCH = IResponsibility.DISPATCH;
    @Override
    public void performAction(Attachment attachment, int response)
    {
        if (response != DISPATCH)
        {
            new EchoBack().performAction(attachment, response);
            return ;
        }
        int id = getDestination(attachment.message);
        int srcId = getSource(attachment.message);
        if (srcId != attachment.clientId)
        {
            System.out.println("src = " + srcId + " clientId = "+ attachment.clientId);
            new EchoBack().performAction(attachment, IResponsibility.ECHOBACK);
            return ;
        }  
        try
        {
            if (attachment.client.isOpen() && Router.getSize() > 1)
            {
                Attachment _attachment = Router.getClient(id);
                if (_attachment == null)
                {
                    new EchoBack().performAction(attachment, IResponsibility.ECHOBACK);
                    return ;
                }
                _attachment.isRead = false;
                _attachment.client.write(attachment.buffer, _attachment, attachment.rwHandler);
            }
        }
        catch(Exception e)
        {
            new EchoBack().performAction(attachment, IResponsibility.ECHOBACK);
        }
    }
    private int getDestination(String datum[])
    {
        try
        {
            for(int i = 0; i < datum.length; i++)
            {
                if (datum[i].contains("56"))
                    return Integer.parseInt(datum[i].split("=")[1]);
            }
        }
        catch(Exception e)
        {

        }
        return -1;
    }
    private int getSource(String datum[])
    {
        try
        {
            if (datum[0].split("=")[0].equalsIgnoreCase("id"))
                return Integer.parseInt(datum[0].split("=")[1]);
        }
        catch(Exception e)
        {
        }
        return -1;
    }
}