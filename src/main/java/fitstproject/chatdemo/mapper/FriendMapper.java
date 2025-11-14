package fitstproject.chatdemo.mapper;

import fitstproject.chatdemo.pojo.FriendRelation;
import fitstproject.chatdemo.pojo.LogUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendMapper {

    // 添加好友关系
    @Insert("INSERT INTO friend_relation(user_id, friend_id) VALUES(#{userId}, #{friendId})")
    void addFriend(FriendRelation friendRelation);

    // 获取用户的所有好友列表
    @Select("SELECT u.id, u.username FROM user u " +
            "INNER JOIN friend_relation fr ON u.id = fr.friend_id " +
            "WHERE fr.user_id = #{userId}")
    List<LogUser> getFriendList(@Param("userId") Integer userId);

    // 检查好友关系是否存在
    @Select("SELECT COUNT(*) FROM friend_relation " +
            "WHERE user_id = #{userId} AND friend_id = #{friendId}")
    int checkFriendExists(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    // 删除好友关系
    @Delete("DELETE FROM friend_relation WHERE user_id = #{userId} AND friend_id = #{friendId}")
    void deleteFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
}
