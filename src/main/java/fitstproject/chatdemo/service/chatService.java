package fitstproject.chatdemo.service;

import fitstproject.chatdemo.pojo.group;
import fitstproject.chatdemo.pojo.groupmessage;
import fitstproject.chatdemo.pojo.message;

public interface chatService {
    Object send(message message);

    Object getslist(int sendId, int receiveId);

    Object groupCreate(group group);

    Object send(groupmessage gmessage);

    Object getlist(int sendId, String groupName);

    Object getslist_name(int id);

    java.util.List<String> getlist_name(int id);

    java.util.List<Integer> getGroupMembers(String groupName);

    boolean checkGroupExists(String groupName);

    void leaveGroup(Integer userId, String groupName);
}
