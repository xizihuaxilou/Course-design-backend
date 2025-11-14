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
        for (Integer group : groups.getMemberIds())
            chatmapper.creategroup(group, groups.getGroupName());
        return null;
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
    public Object getlist_name(int id) {
        return chatmapper.getlist_name(id);
    }

}
