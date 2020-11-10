package main.java.routerwork;

public class CheckSum implements IResponsibility
{
    private int CHECKSUM = IResponsibility.CHECKSUM;
    @Override
    public void performAction(Attachment attachment, int response) {
        if (response != CHECKSUM) {
            new ForwardTo().performAction(attachment, response);
            return ;
        }

        int size = getMessageSize(attachment.message);
        int checksum = getCheckSum(attachment.message[attachment.message.length - 1]);
        int action = IResponsibility.ECHOBACK;
        if (size % 256 != checksum)
            action = IResponsibility.ECHOBACK;
        else
            action = IResponsibility.DISPATCH;
        new ForwardTo().performAction(attachment, action);
    }

    private int getMessageSize(String datum[]) {
        int a = 0;
		char c[];

		for(int b = 0; b < datum.length - 1; b++) {
			c = datum[b].toCharArray();
			for(int d = 0; d < c.length; d++) {
				a += (int)c[d];
			}
			a += 1;
		}
        return (a);
    }

    private int getCheckSum(String part) {
        int tag, value;
        try {
            String ops[] = part.split("=");
            tag = Integer.parseInt(ops[0]);
            value = Integer.parseInt(ops[1]);
            if (tag == 10)
                return value;
            }
        catch(Exception e) {
        System.out.println("Error message passed");
        }
        return (0);
    }
}