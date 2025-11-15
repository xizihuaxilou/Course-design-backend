package fitstproject.chatdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogUser {
    private String username;
    private String password;
    private Integer id;
    // 个人资料字段
    private String realName; // 真实姓名
    private String constellation; // 星座
    private String birthday; // 生日
    private String interests; // 兴趣爱好
}
