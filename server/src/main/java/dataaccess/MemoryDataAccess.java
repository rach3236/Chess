package dataaccess;

import datamodel.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, UserData> sessionInfo = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void addUser(UserData user, String auth) {
        users.put(user.username(), user);
        sessionInfo.put(auth, user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }


    @Override
    public void deleteSessionInfo(String auth) {
        sessionInfo.remove(auth);
    }

    @Override
    public UserData getSessionInfo(String auth) {
        return sessionInfo.get(auth);
    }
}
