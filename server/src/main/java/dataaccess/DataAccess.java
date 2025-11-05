package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    void clear();
    // insert user
    void addUser(UserData user, String auth);
    public UserData getUser(String username);
    public void deleteSessionInfo(String auth);
    public UserData getSessionInfo(String auth);


}
