package osama.parttwoservertcp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
 @FXML
 public TextField ServerPort;
public ArrayList<Users>Users=new ArrayList<Users>();
boolean serverStart=false;
 private ServerSocket serverSocket;
 private boolean listening = true;
 @FXML
 public VBox ServerBoard;
public void StartListen(ActionEvent e) {

  if (!serverStart && !ServerPort.getText().isEmpty()) {
   serverStart = true;
   new Thread(() -> {
    try {
     serverSocket = new ServerSocket(Integer.parseInt(ServerPort.getText()));
     System.out.println("Server started.");

     while (true) {
      Socket clientSocket = serverSocket.accept();

      new Thread(() -> {
       try {
        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        String text;

        while ((text = reader.readLine()) != null) {
         System.out.println("Received from client: " + text);
         String []FullRequest=text.split("//");
         System.out.println(FullRequest[0]);

         if(FullRequest[0].equals("Login")) {
          //Login Request
          text="inF";
          String []userInfo=FullRequest[1].split("-");
          for (int i=0;Users.size()>i;i++){
           if(userInfo[0].toLowerCase().equals(Users.get(i).getUserName().toLowerCase())&&userInfo[1].toLowerCase().equals(Users.get(i).getPassword().toLowerCase())){

            text="Success//"+Users.get(i).getPortNumber()+"-"+Users.get(i).getLastSuccessLogin()+"-"+Users.get(i).getColor()+getAllOnline();
            Date currentDate = new Date();
            Users.get(i).setOnline(true);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String formattedDate = dateFormat.format(currentDate);
            Users.get(i).setLastSuccessLogin(formattedDate);
            UpdateFile();
            String Server=Users.get(i).getUserName()+"Has login successfully.";
            String c=Users.get(i).getColor();
            Platform.runLater(() -> {
             reloadAllOnline();
            Text t=new Text();
            t.setText(Server);
            t.setFill(Color.SNOW);

            ServerBoard.getChildren().add(t);
           });
           }
          }
         } else if (FullRequest[0].equals("Logout")) {
          for(int i=0;i<Users.size();i++){
           if(Users.get(i).getUserName().toLowerCase().equals(FullRequest[1].toLowerCase())){
            Users.get(i).setOnline(false);
            String t=Users.get(i).getUserName()+" Has Logged out";
            Platform.runLater(() -> {
             reloadAllOnline();
             updateOnlineToClients();
             Text tt=new Text(t);
             tt.setFill(Color.SNOW);
             ServerBoard.getChildren().add(tt);

            });
           }
          }
         } else if (FullRequest[0].equals("toAll")) {
          writer.println("d");
          sendToAll(text);

         } else {
          text="Field//Password or Username are invalid ";
         }

         writer.println(text);

         updateOnlineToClients();

        }

        clientSocket.close();
       } catch (IOException ex) {
        System.err.println("Server exception: " + ex.getMessage());
       }
      }).start();
     }
    } catch (IOException ex) {
     ex.printStackTrace();
     System.err.println("Server exception: " + ex.getMessage());
    }
   }).start();
  }
 }


public void send(ActionEvent e){
  /*String hostname = "localhost";
  int port = 12345;

  try (Socket socket = new Socket(hostname, port)) {
   OutputStream output = socket.getOutputStream();
   PrintWriter writer = new PrintWriter(output, true);
   writer.println("LogIn%mohammed,1235");
   InputStream input = socket.getInputStream();
   BufferedReader reader = new BufferedReader(new InputStreamReader(input));
   String response = reader.readLine();
   System.out.println("Server response: " + response);
  } catch (UnknownHostException ex) {
   System.err.println("Server not found: " + ex.getMessage());
  } catch (IOException ex) {
   System.err.println("I/O error: " + ex.getMessage());
  }*/
 }



 @Override
 public void initialize(URL url, ResourceBundle resourceBundle) {
 try (BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\Users\\osama\\Desktop\\PartTwoServerTCP\\src\\main\\resources\\osama\\parttwoservertcp\\Users.txt"))) {
   String line;
   while ((line = reader.readLine()) != null) {
    String []temparray=line.split("-");
    Users A=new Users(temparray[0],temparray[1],Integer.parseInt(temparray[2]),temparray[3]);
    A.setColor(temparray[4]);
    Users.add(A);
   }
   System.out.println(Users.get(1).getUserName());
  } catch (IOException e) {
   System.err.println("Error reading the file: " + e.getMessage());
  }

 }


 public String getAllOnline(){
 String all="";
 for (int i=0;i<Users.size();i++){
  if(Users.get(i).getOnline()){
   all+="//"+Users.get(i).getUserName()+"-"+Users.get(i).getPortNumber()+"-"+Users.get(i).getColor();
  }
 }
 return all;
 }

 public void UpdateFile() {
  String filePath = "Users.txt";

  try (FileWriter fileWriter = new FileWriter(filePath, false);
       BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

   for (int i = 0; i < Users.size(); i++) {

    String userInfo = Users.get(i).getUserName() + "-" +
            Users.get(i).getPassword() + "-" +
            Users.get(i).getPortNumber() + "-" +
            Users.get(i).getLastSuccessLogin()+"-"+
            Users.get(i).getColor();


    bufferedWriter.write(userInfo);
    bufferedWriter.newLine();
   }

   System.out.println("Successfully updated the file with user information.");
  } catch (IOException e) {
   System.out.println("An error occurred while writing to the file.");
   e.printStackTrace();
  }
 }
 public void updateOnlineToClients(){
String online="online,";
ArrayList<Integer> onlineuser=new ArrayList<Integer>();
for (int i=0;i<Users.size();i++) {
 if (Users.get(i).getOnline()) {
  online += Users.get(i).getUserName() + "-" + Users.get(i).getPortNumber() + "-" + Users.get(i).getColor()+"//";
  onlineuser.add(i);
 }
}
for(int i=0;i<onlineuser.size();i++) {
 int order = onlineuser.get(i);
 int port = Users.get(order).getPortNumber();
 String ServerName = "localhost";
 try {
  DatagramSocket SendMessage = new DatagramSocket();
  byte[] MessageInByteFormat = online.getBytes();
  InetAddress IPAddress = InetAddress.getByName(ServerName);
  DatagramPacket sendPacket = new DatagramPacket(MessageInByteFormat, MessageInByteFormat.length, IPAddress, port);
  SendMessage.send(sendPacket);
  SendMessage.close();
 } catch (Exception d) {
  System.err.println("in send btn");
 }

}
}
@FXML
public Pane CreatePane;
@FXML
public TextField PortNumberText;
 @FXML
 public TextField PasswordText;
 @FXML
 public TextField UsernameText;
 @FXML
 public TextField ColorText;
public void OpenCreate(){
 CreatePane.setVisible(true);
}

public void CloseCreate(){
 CreatePane.setVisible(false);
}
public void AddNewButton(){
 if(!(UsernameText.getText().isEmpty()||PasswordText.getText().isEmpty()||PortNumberText.getText().isEmpty()||ColorText.getText().isEmpty())) {
  boolean isNew = true;
  for (int i = 0; i < Users.size(); i++) {
   if (Users.get(i).getUserName().toLowerCase().equals(UsernameText.getText().toLowerCase())) {
    isNew = false;
   }
  }


  if (isNew) {

   Users n = new Users(UsernameText.getText(), PasswordText.getText(), Integer.parseInt(PortNumberText.getText()), "now");
   n.setColor(ColorText.getText());
   Users.add(n);
   UpdateFile();
   UsernameText.setText("");
   PasswordText.setText("");
   PortNumberText.setText("");
   ColorText.setText("");
   String Server="New User Has Been Created";
   Text t=new Text(Server);
   t.setFill(Color.SNOW);
   ServerBoard.getChildren().add(t);
   JOptionPane.showMessageDialog(null, "User Have Been Created Successfully  ", "Done", JOptionPane.INFORMATION_MESSAGE);
  }
  else {
   JOptionPane.showMessageDialog(null, "The name is allredy taken  ", "Eror", JOptionPane.ERROR_MESSAGE);

  }
 }
 else{
  JOptionPane.showMessageDialog(null, "You should fill all Fields  ", "Erorr", JOptionPane.ERROR_MESSAGE);

 }
}

@FXML
public VBox VBoxOnline;
public void reloadAllOnline(){
 VBoxOnline.getChildren().clear();
 for (int i=0;i<Users.size();i++){
  if(Users.get(i).getOnline()){
   String server=Users.get(i).getUserName()+"-"+Users.get(i).getPortNumber();
   Text t=new Text(server);
   t.setStyle("-fx-fill: "+Users.get(i).getColor()+";");


   VBoxOnline.getChildren().add(t);
  }
 }
}


public void sendToAll(String meg){
 System.out.println(meg);
 String []a=meg.split("//");
 String b=a[0]+","+a[1]+"//"+a[2];
 System.out.println(meg);
 System.out.println(a[0]);
 System.out.println(a[1]);
 for (int i=0;i<Users.size();i++){
  if (Users.get(i).getOnline()){

   int port = Users.get(i).getPortNumber();
   String ServerName = "localhost";
   try {
    DatagramSocket SendMessage = new DatagramSocket();
    byte[] MessageInByteFormat = b.getBytes();
    InetAddress IPAddress = InetAddress.getByName(ServerName);
    DatagramPacket sendPacket = new DatagramPacket(MessageInByteFormat, MessageInByteFormat.length, IPAddress, port);
    SendMessage.send(sendPacket);
    SendMessage.close();
   } catch (Exception d) {
    System.err.println("in send btn");
   }
  }
 }
}
}
