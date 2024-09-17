package UDP.Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import model.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
public class ServerControl {

    private Connection con;
    private DatagramSocket myServer;
    private int serverPort = 5555;
    private DatagramPacket receivePacket = null;

    public ServerControl() throws SQLException {
        getDBConnection("test", "root", "");
        openServer(serverPort);
        while (true) {
            listenning();
        }
    }

    private void getDBConnection(String dbName, String username, String password) {

        String dbUrl = "jdbc:mysql://localhost:3306/" + dbName;
        String dbClass = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(dbClass);
            con = DriverManager.getConnection(dbUrl, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openServer(int portNumber) {
        try {
            myServer = new DatagramSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenning() throws SQLException {
        DataSend data = receiveData();
        String result = "false";
        if (data.getMethod() == 0) {
            if (checkUser(data.getUser())) {
                result = "ok";
            } else {
                result = "fail";
            }
            sendData(result);
        } else if (data.getMethod() == 1) {
            if (checkUserName(data.getUser().getUserName())) {
                result = "exist";
            } else {
                if(registerUser(data.getUser()))
                { 
                    result = "ok";
                }
                else{
                    result = "fail";
                }
            }
            sendData(result);
        } else if (data.getMethod() == 2) {
            ArrayList<User> users = getUserByUserName(data.getUser().getUserName());
            sendListUser(users);
        }
    }
    
    
    public ArrayList<User> getUserByUserName(String keyWord) {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE username LIKE ?";
         
        
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + keyWord + "%");
            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            // Process the results
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(username, password);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return users;
    }
    private boolean registerUser(User user) throws SQLException {
        String query = "INSERT INTO user (username, password) VALUES (?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private void sendData(String result) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(result);
            oos.flush();
            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, IPAddress, clientPort);

            myServer.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendListUser(ArrayList<User> users)
    {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(users);
            oos.flush();
            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, IPAddress, clientPort);
            myServer.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private DataSend receiveData() {
        DataSend user = null;
        try {

            byte[] receiveData = new byte[1024];

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            myServer.receive(receivePacket);
            ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);

            ObjectInputStream ois = new ObjectInputStream(bais);
            user = (DataSend) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    private boolean checkUser(User user) {
        String query = "Select * FROM user WHERE username ='"
                + user.getUserName()
                + "' AND password ='" + user.getPassword() + "'";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserName(String username) {
        String query = "Select * FROM user WHERE username ='"
                + username + "';";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
