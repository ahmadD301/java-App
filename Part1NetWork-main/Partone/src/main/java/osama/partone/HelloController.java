package osama.partone;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HelloController implements Initializable {

    public Thread UDPListen;
    public DatagramSocket UDPSocket;
public boolean threadClick=false;
    @FXML
    public ChoiceBox<String> ABox;
    @FXML
    public Pane ArchivePane;

    @FXML
    public Button ArchiveButton;
    @FXML
    public VBox VboxMessages;
    @FXML
    public VBox VBoxTime;
    @FXML
    public TextField LocalIP;
    @FXML
    public TextField RemotePort;
    @FXML
    public TextField LocalPort;
    @FXML
    public Button ConnectButton;
    public boolean StopThread=false;
    @FXML
    public VBox VBoxArchiveTime;
    public Thread Timer;
    public boolean timerStart=false;
    @FXML
    public VBox VBoxArchiveMessages;
@FXML
public TextArea MessageArea;
public ArrayList<MessageObj> allMessages = new ArrayList<>();
public ArrayList<Integer> ClickedMessage=new ArrayList<>();
@FXML
public TextField RemoteIP;
public ArrayList<Integer> ClickedMessageInArchive=new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Enumeration<NetworkInterface> Interfacs = null;
        try {
            Interfacs = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            System.out.println("first catch");
        }
        for (NetworkInterface netInterface : Collections.list(Interfacs)) {
            try {
                if (netInterface.isUp() && !netInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
                            String displayName = netInterface.getDisplayName() + " (" + addr.getHostAddress() + ")";
                            ABox.getItems().add(displayName);

                        }
                    }
                }
            } catch (SocketException e) {
                System.out.println("in intialize");
            }
        }
        ABox.setOnAction(this::SetLocalIp);
    }

    private void SetLocalIp(ActionEvent actionEvent) {
        String Ip= String.valueOf(ABox.valueProperty());
        String [] UserIp=Ip.split("\\(");
        String[]UserIpFinal=UserIp[UserIp.length-1].split("\\)");
        LocalIP.setText(UserIpFinal[0]);
        RemoteIP.setText(UserIpFinal[0]);
    }
    public void logActivity(String message) {
        try (FileWriter fileWriter = new FileWriter("chat_log.txt", true);
             PrintWriter logWriter = new PrintWriter(fileWriter)) {

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logWriter.println(timeStamp + " - " + message);
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }

    public void ConnectButtonFunction(ActionEvent e){

        if(LocalIP.getText().isEmpty()||RemotePort.getText().isEmpty()||LocalPort.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Fill all fields including Port numbers and select interface", "Missing Information", JOptionPane.WARNING_MESSAGE);

        }else {
            if (!threadClick) {
                logActivity("Connection Opend: ");
                threadClick=true;
                StopThread=false;
                System.out.println("ff");
                UDPListen = new Thread(() -> {
                    try {

                        UDPSocket = new DatagramSocket(Integer.parseInt(LocalPort.getText()));
                        byte[] buffer = new byte[1024];
                        while(true) {
                            if(!StopThread) {
                                DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
                                UDPSocket.receive(pack);
                                String recived = new String(pack.getData(), 0, pack.getLength());
                                System.out.println(recived);
                                String[] Divide=recived.split(",");
                                if(Divide[0].equals("Delete")){
                                    System.out.println("here 2");
                                    String[] IndexsForArchive=Divide[1].split("&");
                                    for(int i=0;i<IndexsForArchive.length;i++){
                                        System.out.println(IndexsForArchive[i]);
                                        allMessages.get(Integer.parseInt(IndexsForArchive[i])).setState("Archive");
                                    }
                                    System.out.println("hmm");
                                    Platform.runLater(() -> {
                                        min=1;
                                        sec=0;
                                        TimerStop = false;
                                        TimerThread();
                                    ReloadMessages();
                                    LoadArchive();
                                    });
                                }else if(Divide[0].equals("Restore")){
                                    logActivity("MSG Restored From Archive: ");
                                    System.out.println("here 2");

                                    String[] IndexsForRestoreArchive=Divide[1].split("&");
                                    for(int i=0;i<IndexsForRestoreArchive.length;i++){
                                        System.out.println(IndexsForRestoreArchive[i]);
                                        allMessages.get(Integer.parseInt(IndexsForRestoreArchive[i])).setState("Chat");
                                    }
                                    System.out.println("hmm");
                                    Platform.runLater(() -> {
                                        ReloadMessages();
                                        LoadArchive();
                                    });
                                }
                                else{
                                //create message Element
                                Platform.runLater(() -> {
                                    MessageObj msg=new MessageObj(recived,allMessages.size(),"Friend","ME","Chat");
                                    allMessages.add(msg);
                                    Text OrderOfMessage=new Text(msg.getOrder()+"");
                                    OrderOfMessage.setVisible(false);
                                    Text text = new Text(msg.getSender()+" : "+msg.getMessage());
                                    text.setWrappingWidth(600);
                                    text.setLayoutY(14);
                                    text.setFill(Color.FIREBRICK);
                                    Pane Cont=new Pane();
                                    Cont.setPrefSize(350,20);
                                    Cont.getChildren().add(text);
                                    Cont.getChildren().add(OrderOfMessage);
                                    Cont.setOnMouseClicked(Event -> MessageClick(msg.getOrder(),Cont));
                                    //Cont.setOnMouseEntered(Event -> Cont.setStyle("-fx-background-color: #DDDDDD;"));
                                    //Cont.setOnMouseExited(Event -> Cont.setStyle("-fx-background-color: transparent;"));
                                    VboxMessages.getChildren().add(Cont);
                                    Text timeText=new Text(msg.getTimeStamp());
                                    timeText.setFill(Color.SNOW);
                                    Pane ContTime=new Pane(timeText);
                                    timeText.setLayoutY(14);
                                    ContTime.setPrefSize(50,20);

                                    VBoxTime.getChildren().add(ContTime);
                                });}



                            }else{System.out.println("connection close");
                                logActivity("Connection Close: ");
                                UDPSocket.close();
                                break;
                            }
                        }
                    } catch (Exception r) {

                        JOptionPane.showMessageDialog(null, "There is an exciption please reconnect again (in Listener) ", "Error", JOptionPane.WARNING_MESSAGE);

                    }
                });
                UDPListen.start();
                System.out.println(Thread.activeCount());
            }
            else{
                JOptionPane.showMessageDialog(null, "You should first close the connection", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void SendButtonFunction(ActionEvent e){
        if(!StopThread) {
            if (MessageArea.getText().isEmpty()||LocalPort.getText().isEmpty()||RemotePort.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "You should Enter Message", "Error", JOptionPane.WARNING_MESSAGE);

            } else {

                String Message = MessageArea.getText();
                MessageArea.setText("");
                logActivity("Message Sent: " + Message);
                int SendToPort = Integer.parseInt(RemotePort.getText());
                String ServerName = "localhost";
                try {
                    DatagramSocket SendMessage = new DatagramSocket();
                    byte[] MessageInByteFormat = Message.getBytes();
                    InetAddress IPAddress = InetAddress.getByName(ServerName);
                    DatagramPacket sendPacket = new DatagramPacket(MessageInByteFormat, MessageInByteFormat.length, IPAddress, SendToPort);
                    SendMessage.send(sendPacket);
                    SendMessage.close();
                    Platform.runLater(() -> {
                        MessageObj msg=new MessageObj(Message,allMessages.size(),"ME","Friend","Chat");
                        allMessages.add(msg);
                        Text text = new Text(msg.getSender()+" : "+ msg.getMessage());
                        text.setWrappingWidth(600);
                        text.setLayoutY(14);
                        Text OrderOfMessage= new Text(msg.getOrder()+"");
                        OrderOfMessage.setVisible(false);

                        text.setFill(Color.SKYBLUE);
                        Pane Cont=new Pane();

                        Cont.setPrefSize(350,20);

                        Cont.getChildren().add(text);
                        Cont.getChildren().add(OrderOfMessage);
                        //Cont.setOnMouseEntered(Event -> Cont.setStyle("-fx-background-color: #DDDDDD;"));
                        //Cont.setOnMouseExited(Event -> Cont.setStyle("-fx-background-color: transparent;"));
                        Cont.setOnMouseClicked(Event -> MessageClick(msg.getOrder(),Cont));

                        VboxMessages.getChildren().add(Cont);
                        Text timeText=new Text(msg.getTimeStamp());
                        timeText.setFill(Color.SNOW);
                        Pane ContTime=new Pane(timeText);
                        timeText.setLayoutY(14);
                        ContTime.setPrefSize(50,20);

                        VBoxTime.getChildren().add(ContTime);
                    });
                } catch (Exception d) {
                    System.err.println("in send btn");
                }

            }
        }else{
            JOptionPane.showMessageDialog(null, "There is no connection ", "Error", JOptionPane.WARNING_MESSAGE);

        }
    }
public void CloseConnectionButton(ActionEvent e){

    if(threadClick){
        logActivity("Connection Closed: ");
        allMessages.clear();
        VBoxArchiveTime.getChildren().clear();
        VBoxArchiveMessages.getChildren().clear();
        UpdateArchiveCounter();
        threadClick=false;
        StopThread=true;
        RemotePort.setText("");
        LocalPort.setText("");
        VboxMessages.getChildren().clear();
        VBoxTime.getChildren().clear();
        for(int i=0;i<allMessages.size();i++){
            System.out.println(allMessages.get(i).getMessage()+allMessages.get(i).getOrder());
        }
    }
    else{
        JOptionPane.showMessageDialog(null, "There is no connection ", "Error", JOptionPane.WARNING_MESSAGE);

    }
}
public void MessageClick(int order,Pane pane){
        Text OrderOfClickedMessage= (Text) pane.getChildren().get(1);
        ClickedMessage.add(Integer.valueOf(OrderOfClickedMessage.getText()));
    System.out.println(ClickedMessage.toString());
        pane.setStyle("-fx-background-color: #CCCCCC");
        ((Text) pane.getChildren().get(1)).setFill(Color.BLACK);
}


public void AddToArchiveButton(ActionEvent e){
        min=2;
        sec=0;
    TimerStop = false;
    TimerThread();

        if(ClickedMessage.size()!=0) {
            int SendToPort = Integer.parseInt(RemotePort.getText());
            String IndexOfDelete = "Delete,";
            for (int i = 0; i < ClickedMessage.size(); i++) {
                allMessages.get(ClickedMessage.get(i)).setState("Archive");
                IndexOfDelete += ClickedMessage.get(i) + "&";
            }
            ClickedMessage.clear();
            try {
                logActivity("MSG Archive: ");
                DatagramSocket SendMessage = new DatagramSocket();
                byte[] MessageInByteFormat = IndexOfDelete.getBytes();
                InetAddress IPAddress = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(MessageInByteFormat, MessageInByteFormat.length, IPAddress, SendToPort);
                SendMessage.send(sendPacket);
                SendMessage.close();
            } catch (Exception Event) {
                System.err.println("in Add to archive ");
                JOptionPane.showMessageDialog(null, "Make sure you fill all fields", "Error", JOptionPane.WARNING_MESSAGE);

            }
            ReloadMessages();
        }
}


public void ReloadMessages(){
    UpdateArchiveCounter();
        VboxMessages.getChildren().clear();
        VBoxTime.getChildren().clear();
        for(int i=0;i<allMessages.size();i++){
            if(allMessages.get(i).getState().equals("Chat")){
                Text text = new Text(allMessages.get(i).getSender()+" : "+allMessages.get(i).getMessage());
                text.setWrappingWidth(600);
                text.setLayoutY(14);
                Text OrderOfMessage= new Text(allMessages.get(i).getOrder()+"");
                OrderOfMessage.setVisible(false);
                if(allMessages.get(i).getSender().equals("ME")) {
                    text.setFill(Color.SKYBLUE);
                }
                else{
                    text.setFill(Color.FIREBRICK);
                }
                Pane Cont=new Pane();
                Cont.setPrefSize(350,20);
                Cont.getChildren().add(text);
                Cont.getChildren().add(OrderOfMessage);
                //Cont.setOnMouseEntered(Event -> Cont.setStyle("-fx-background-color: #DDDDDD;"));
                //Cont.setOnMouseExited(Event -> Cont.setStyle("-fx-background-color: transparent;"));
                int finalI = i;
                Cont.setOnMouseClicked(Event -> MessageClick(allMessages.get(finalI).getOrder(),Cont));

                VboxMessages.getChildren().add(Cont);
                Text timeText=new Text(allMessages.get(i).getTimeStamp());
                timeText.setFill(Color.SNOW);
                Pane ContTime=new Pane(timeText);
                timeText.setLayoutY(14);
                ContTime.setPrefSize(50,20);

                VBoxTime.getChildren().add(ContTime);
            }
        }
}
public void UpdateArchiveCounter(){
        int count=0;
    for (int i = 0; i < allMessages.size(); i++) {
        if (allMessages.get(i).getState().equals("Archive")){
            count++;
        }
    }
    ArchiveButton.setText("Archive("+count+")");
}
public void ArchiveShowButton(ActionEvent e){
    ArchivePane.setVisible(true);
    LoadArchive();
}
public void BackToMainPage(ActionEvent e){
        ClickedMessageInArchive.clear();
        ClickedMessage.clear();
        ArchivePane.setVisible(false);
}
public void LoadArchive(){
        VBoxArchiveTime.getChildren().clear();
        VBoxArchiveMessages.getChildren().clear();
for(int i=0;i<allMessages.size();i++){
    if(allMessages.get(i).getState().equals("Archive")){
        Text text = new Text(allMessages.get(i).getSender()+" : "+allMessages.get(i).getMessage());
        text.setWrappingWidth(600);
        text.setLayoutY(14);
        Text OrderOfMessage= new Text(allMessages.get(i).getOrder()+"");
        OrderOfMessage.setVisible(false);
        if(allMessages.get(i).getSender().equals("ME")) {
            text.setFill(Color.SKYBLUE);
        }
        else{
            text.setFill(Color.FIREBRICK);
        }
        Pane Cont=new Pane();
        Cont.setPrefSize(350,20);
        Cont.getChildren().add(text);
        Cont.getChildren().add(OrderOfMessage);
        //Cont.setOnMouseEntered(Event -> Cont.setStyle("-fx-background-color: #DDDDDD;"));
        //Cont.setOnMouseExited(Event -> Cont.setStyle("-fx-background-color: transparent;"));
        int finalI = i;
        Cont.setOnMouseClicked(Event -> MessageClickInArchive(allMessages.get(finalI).getOrder(),Cont));

        VBoxArchiveMessages.getChildren().add(Cont);
        Text timeText=new Text(allMessages.get(i).getTimeStamp());
        timeText.setFill(Color.SNOW);
        Pane ContTime=new Pane(timeText);
        timeText.setLayoutY(14);
        ContTime.setPrefSize(50,20);

        VBoxArchiveTime.getChildren().add(ContTime);
    }
}
}
    public void MessageClickInArchive(int order,Pane pane){
        Text OrderOfClickedMessage= (Text) pane.getChildren().get(1);
        ClickedMessageInArchive.add(Integer.valueOf(OrderOfClickedMessage.getText()));
        System.out.println(ClickedMessageInArchive.toString());
        pane.setStyle("-fx-background-color: #CCCCCC");
    }
    public void RestoreClickedMessage(ActionEvent e){
        int SendToPort = Integer.parseInt(RemotePort.getText());
        String IndexOfDelete="Restore,";
        for(int i=0;i<ClickedMessageInArchive.size();i++){
            allMessages.get(ClickedMessageInArchive.get(i)).setState("Chat");
            IndexOfDelete+=ClickedMessageInArchive.get(i)+"&";
        }
        ClickedMessageInArchive.clear();
        try {
            DatagramSocket SendMessage = new DatagramSocket();
            byte[] MessageInByteFormat = IndexOfDelete.getBytes();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(MessageInByteFormat, MessageInByteFormat.length, IPAddress, SendToPort);
            SendMessage.send(sendPacket);
            SendMessage.close();
        }catch (Exception Event){
            System.err.println("in Add to archive ");
            JOptionPane.showMessageDialog(null, "Make sure you fill all fields", "Error", JOptionPane.WARNING_MESSAGE);

        }
        ReloadMessages();
        LoadArchive();
    }
    public void ArchiveAllMessages(ActionEvent e){

        min=1;
        sec=0;
        TimerStop = false;
        TimerThread();
        for(int i=0 ;i<allMessages.size();i++){
            if(allMessages.get(i).getState().equals("Chat")) {
                allMessages.get(i).setState("Archive");
                ClickedMessage.add(allMessages.get(i).getOrder());
            }
        }
        LoadArchive();
        ReloadMessages();
        AddToArchiveButton(e);
    }
    @FXML
    public TextField counter;
public boolean TimerStop=true;
@FXML
public Text DeleteText;
    public int min=2 ;
    public int sec=0;
    public void TimerThread(){
        if(!timerStart) {
            timerStart=true;
            System.out.println("here");
            Timer = new Thread(() -> {
                System.out.println("hmmm");
                TimerStop = false;
                System.out.println("here");
                while (true) {
                    if (!TimerStop) {
                        System.out.println("here2");

                        try {
                            if (min == 0 && sec == 0) {
                                TimerStop = true;
                                Platform.runLater(() -> {
                                VBoxArchiveMessages.getChildren().clear();
                                VBoxArchiveTime.getChildren().clear();
                                for(int i=0;i<allMessages.size();i++){
                                    if(allMessages.get(i).getState().equals("Archive")){
                                        allMessages.get(i).setState("Delete");
                                        ClickedMessage.clear();
                                        ClickedMessageInArchive.clear();
                                    }
                                }
                                    UpdateArchiveCounter();
                                });
                            } else {
                                if (sec - 1 < 0 && min > 0) {
                                    min--;
                                    sec = 60;
                                    System.out.println("here");

                                }
                                sec--;
                                Platform.runLater(() -> {
                                    if (sec > 10) {
                                        DeleteText.setText("All Messages will automatic Delete in :"+"0" + min + ":" + sec + "");
                                    } else {
                                        DeleteText.setText("All Messages will automatic Delete in :"+"0" + min + ":0" + sec);
                                    }
                                });
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println("in timer");
                        }
                    }else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            Timer.start();
        }

    }
}
