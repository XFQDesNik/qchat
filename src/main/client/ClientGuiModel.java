package main.client;

import java.util.Set;
import java.util.HashSet;

public class ClientGuiModel {
    private final Set<String> allUserNames = new HashSet<String>();
    private String newMessage;

    public Set<String> getAllUserNames () {
        return allUserNames;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public void addUser(String newUserName) {
        allUserNames.add(newUserName);
    }

    public void deleteUser(String userName) {
        allUserNames.remove(userName);
    }
}