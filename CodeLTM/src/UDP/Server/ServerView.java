package UDP.Server;

import java.sql.SQLException;

public class ServerView {

    public ServerView() throws SQLException {
        new ServerControl();
        showMessage("UDP server is running...");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }
}
