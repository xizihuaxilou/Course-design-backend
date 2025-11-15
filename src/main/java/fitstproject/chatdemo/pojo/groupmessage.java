package fitstproject.chatdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class groupmessage {
    private Integer id;
    private Integer sendId;
    private String content;
    private String groupName;
    private LocalDateTime createTime;
}
