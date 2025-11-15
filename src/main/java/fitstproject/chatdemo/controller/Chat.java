package fitstproject.chatdemo.controller;

import fitstproject.chatdemo.mapper.FriendMapper;
import fitstproject.chatdemo.pojo.*;
import fitstproject.chatdemo.service.chatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/chat")
@RestController
public class Chat {

    @Autowired
    chatService chatservice;

    @Autowired
    FriendMapper friendMapper;

    @GetMapping("/single/name_list/{id}")
    public Object singleListName(@PathVariable int id) {
        return chatservice.getslist_name(id);
    }

    @PostMapping("/single/send")
    public Object singleSend(@RequestBody message message) {
        System.out.println(message);
        return chatservice.send(message);
    }

    @GetMapping("/single/list/{sendId}/{receiveId}")
    public Object singleList(@PathVariable int sendId, @PathVariable int receiveId) {
        return chatservice.getslist(sendId, receiveId);
    }

    @GetMapping("/group/list_name/{id}")
    public Object groupListName(@PathVariable int id) {
        System.out.println("groupListName");
        return chatservice.getlist_name(id);
    }

    @PostMapping("/group/create")
    public result groupCreate(@RequestBody group group) {
        try {
            chatservice.groupCreate(group);
            return new result("创建群聊成功", "200", null);
        } catch (Exception e) {
            log.error("创建群聊失败", e);
            return new result(e.getMessage() != null ? e.getMessage() : "创建群聊失败", "400", null);
        }
    }

    @PostMapping({ "/group/send" })
    public Object groupSend(@RequestBody groupmessage gmessage) {
        return chatservice.send(gmessage);

    }

    @GetMapping("/group/list/{sendId}/{groupName}")
    public Object groupList(@PathVariable int sendId, @PathVariable String groupName) {
        return chatservice.getlist(sendId, groupName);
    }

    // ========== 好友管理接口 ==========

    /**
     * 添加好友
     * 
     * @param friendRelation {userId: 当前用户ID, friendId: 要添加的好友ID}
     */
    @PostMapping("/friend/add")
    public result addFriend(@RequestBody FriendRelation friendRelation) {
        try {
            // 检查是否已经是好友
            int exists = friendMapper.checkFriendExists(friendRelation.getUserId(), friendRelation.getFriendId());
            if (exists > 0) {
                return new result("已经是好友了", "400", null);
            }

            // 双向添加好友关系
            friendMapper.addFriend(friendRelation);
            FriendRelation reverse = new FriendRelation();
            reverse.setUserId(friendRelation.getFriendId());
            reverse.setFriendId(friendRelation.getUserId());
            friendMapper.addFriend(reverse);

            return new result("添加好友成功", "200", null);
        } catch (Exception e) {
            log.error("添加好友失败", e);
            return new result("添加好友失败: " + e.getMessage(), "500", null);
        }
    }

    /**
     * 获取好友列表
     * 
     * @param userId 用户ID
     */
    @GetMapping("/friend/list/{userId}")
    public List<LogUser> getFriendList(@PathVariable Integer userId) {
        return friendMapper.getFriendList(userId);
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/friend/delete")
    public result deleteFriend(@RequestBody FriendRelation friendRelation) {
        try {
            // 双向删除好友关系
            friendMapper.deleteFriend(friendRelation.getUserId(), friendRelation.getFriendId());
            friendMapper.deleteFriend(friendRelation.getFriendId(), friendRelation.getUserId());
            return new result("删除好友成功", "200", null);
        } catch (Exception e) {
            log.error("删除好友失败", e);
            return new result("删除好友失败: " + e.getMessage(), "500", null);
        }
    }

    /**
     * 获取群成员列表
     */
    @GetMapping("/group/members/{groupName}")
    public int getGroupMembers(@PathVariable String groupName) {
        List<Integer> members = chatservice.getGroupMembers(groupName);
        return members != null ? members.size() : 0;
    }

    /**
     * 退出群聊
     */
    @DeleteMapping("/group/leave")
    public result leaveGroup(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            String groupName = (String) params.get("groupName");

            if (userId == null || groupName == null || groupName.isEmpty()) {
                return new result("参数错误", "400", null);
            }

            chatservice.leaveGroup(userId, groupName);
            return new result("退出群聊成功", "200", null);
        } catch (Exception e) {
            log.error("退出群聊失败", e);
            return new result("退出群聊失败: " + e.getMessage(), "500", null);
        }
    }

}
