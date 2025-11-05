package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    void clear();
    // insert user
    void addUser(UserData user);
    public UserData getUser(String username);


}
