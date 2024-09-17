package TCPIP.Client;

import model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.DataSend;

public class ClientControl {

    private Socket mySocket;
    private String serverHost = "localhost";
    private int serverPort = 8888;

    public ClientControl() {
    }

    public Socket openConnection() {
        try {

            mySocket = new Socket(serverHost, serverPort);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
            return null;
        }

        return mySocket;
    }

    public boolean sendData(DataSend data) {
        try {

            ObjectOutputStream oos
                    = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(data);
            System.out.println("gui du lieu di");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public String receiveData() {
        String result = null;
        try {
            ObjectInputStream ois
                    = new ObjectInputStream(mySocket.getInputStream());
            Object o = ois.readObject();
            if (o instanceof String) {
                result = (String) o;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return result;
    }

    public boolean closeConnection() {
        try {
            mySocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public ArrayList<User> receiveDataUser() {
        ArrayList<User> result = new ArrayList<>();
        try {
            ObjectInputStream ois
                    = new ObjectInputStream(mySocket.getInputStream());
            Object o = ois.readObject();
            if(o instanceof ArrayList)
                result = (ArrayList<User>) o;

        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
            return new ArrayList<User>();
        }

        return result;
    }
}
