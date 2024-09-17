
package model;

import java.io.Serializable;

public class DataSend implements Serializable{
    private User user;
    private int method;

    public DataSend(User user, int method) {
        this.user = user;
        this.method = method;
    }

    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
    
    
    
}
