package fitstproject.chatdemo.mapper;

import fitstproject.chatdemo.pojo.LogUser;
import fitstproject.chatdemo.pojo.group;
import fitstproject.chatdemo.pojo.groupmessage;
import fitstproject.chatdemo.pojo.message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface chatMapper {

    @Insert("insert into singlemessage(send_id, receive_id, content) values(#{sendId},#{receiveId},#{content})")
    void send(message message);

    @Select("select * from singlemessage where (receive_id=#{receiveId} and send_id=#{sendId}) or (receive_id=#{sendId} and send_id=#{receiveId}) order by create_time ASC")
    List<message> getslist(int sendId, int receiveId);

    @Insert("insert into groupdata(group_name, member_id) values(#{groupName},#{group})")
    void creategroup(Integer group, String groupName);

    @Insert("insert into group_message_data(send_id, group_name, content) values(#{sendId},#{groupName},#{content})")
    void sendgm(groupmessage gmessage);

    @Select("select * from group_message_data where group_name=#{groupName}")
    List<groupmessage> getlistgn(int sendId, String groupName);

    @Select("select * from user where id!=#{id}")
    List<LogUser> getslist_name(int id);

    @Select("select group_name from groupdata")
    Object getlist_name(int id);
}
