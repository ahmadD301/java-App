package osama.parttwoservertcp;

import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.text.SimpleDateFormat;
        import java.util.Date;
public class Users {
    private String Color="green";
    private String password;
    private String UserName;
    private int PortNumber;
    private boolean IsOnline=false;
    private String LastSuccessLogin;


    Users(String name,String password,int port,String last){
        this.PortNumber=port;
        this.UserName=name;
        this.password=password;
        this.LastSuccessLogin=last;
    }

public void setColor(String s){
        this.Color=s;

}
public String getColor(){
        return this.Color;
}
    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUserName() {
        return UserName;
    }



    public void setPortNumber(int portNumber) {
        this.PortNumber = portNumber;
    }

    public int getPortNumber() {
        return PortNumber;
    }

    public void setLastSuccessLogin(String lastSuccessLogin) {
        this.LastSuccessLogin = lastSuccessLogin;
    }


    public String getLastSuccessLogin() {
        return LastSuccessLogin;
    }


    public String toSimpleString() {
        return UserName + "." + PortNumber;
    }


    public String toFullString() {

        return UserName + "." + "." + PortNumber + "." + LastSuccessLogin;
    }
    public String getPassword(){
        return this.password;
    }
    public  void setPassword(String password){
        this.password=password;
    }
    public void setOnline(boolean x){
        this.IsOnline=x;
    }
    public boolean getOnline(){
        return this.IsOnline;
    }
}
