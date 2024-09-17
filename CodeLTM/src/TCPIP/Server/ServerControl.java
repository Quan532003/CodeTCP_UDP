package TCPIP.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.*;

public class ServerControl {

    private Connection con;
    private ServerSocket myServer;
    private int serverPort = 8888;

    public ServerControl() {
        getDBConnection("test", "root", "");
        openServer(serverPort);
        while (true) {
            listenning();
        }
    }

    private void getDBConnection(String dbName, String username,
            String password) {

        String dbUrl = "jdbc:mysql://localhost:3306/" + dbName;
        String dbClass = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(dbClass);
            con = DriverManager.getConnection(dbUrl,
                    username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openServer(int portNumber) {
        try {
            myServer = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenning() {
        try {
            Socket clientSocket = myServer.accept();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            Object o = ois.readObject();
            if (o instanceof DataSend) {
                DataSend data = (DataSend) o;
                if (data.getMethod() == 0) {
                    if (checkUser(data.getUser())) {
                        oos.writeObject("ok");
                    } else {
                        oos.writeObject("false");
                    }
                } else if (data.getMethod() == 1) {
                    if (checkUserName(data.getUser().getUserName())) {
                        oos.writeObject("exist");
                    } else {
                        if(registerUser(data.getUser()))
                        {
                            oos.writeObject("ok");
                        }
                        else{
                            oos.writeObject("fail");
                        }
                    }
                } else if (data.getMethod() == 2) {
                    ArrayList<User> users = getUserByUserName(data.getUser().getUserName());
                    System.out.println(users.size());
                    oos.writeObject(users);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private boolean checkUser(User user) throws Exception {
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
            throw e;
        }
        return false;
    }

    public boolean checkUserName(String username) {
        String query = "Select * FROM user WHERE username ='"
                + username + "';";
        System.out.println(query);
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                System.out.println("co ton tai " + username);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            System.out.println(users.size());
        } catch (SQLException e) {
            System.out.println(e);
        }
        return users;
    }
}
