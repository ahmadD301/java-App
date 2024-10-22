package osama.partone;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageObj {
    private String Message="";
    private int Order;
    private String TimeStamp;
    private String State;
    private String Sender;
    private String Receiver;


    public MessageObj(String Msg,int order ,String send,String rs ,String state){
        this.Order=order;
        this.Message=Msg;
        this.Sender=send;
        this.Receiver=rs;
        this.State=state;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date now=new Date();
        this.TimeStamp= formatter.format(now);
        System.out.println(formatter);
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        this.Order = order;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }


    public String getState() {
        return State;
    }

    public void setState(String state) {
        this.State = state;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        this.Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        this.Receiver = receiver;
    }




}
