package fitstproject.chatdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRelation {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private String createdAt;
}
