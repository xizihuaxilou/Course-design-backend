package fitstproject.chatdemo.service.impl;

import fitstproject.chatdemo.mapper.chatMapper;
import fitstproject.chatdemo.pojo.group;
import fitstproject.chatdemo.pojo.groupmessage;
import fitstproject.chatdemo.pojo.message;
import fitstproject.chatdemo.service.chatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class chatServiceimpl implements chatService {
    @Autowired
    chatMapper chatmapper;

    @Override
    public Object send(message message) {
        chatmapper.send(message);

        return null;
    }

    @Override
    public Object getslist(int sendId, int receiveId) {
        return chatmapper.getslist(sendId, receiveId);

    }

    @Override
    public Object groupCreate(group groups) {
        // 检查群名是否已存在
        if (chatmapper.checkGroupExists(groups.getGroupName()) > 0) {
            throw new RuntimeException("群名称已存在，请使用其他名称");
        }

        // 插入群成员记录
        for (Integer group : groups.getMemberIds())
            chatmapper.creategroup(group, groups.getGroupName());
        return null;
    }

    @Override
    public boolean checkGroupExists(String groupName) {
        return chatmapper.checkGroupExists(groupName) > 0;
    }

    @Override
    public Object send(groupmessage gmessage) {
        chatmapper.sendgm(gmessage);
        return null;
    }

    @Override
    public Object getlist(int sendId, String groupName) {
        return chatmapper.getlistgn(sendId, groupName);
    }

    @Override
    public Object getslist_name(int id) {
        return chatmapper.getslist_name(id);
    }

    @Override
    public java.util.List<String> getlist_name(int id) {
        return chatmapper.getlist_name(id);
    }

    @Override
    public java.util.List<Integer> getGroupMembers(String groupName) {
        return chatmapper.getGroupMembers(groupName);
    }

    @Override
    public void leaveGroup(Integer userId, String groupName) {
        chatmapper.leaveGroup(userId, groupName);
    }

}
