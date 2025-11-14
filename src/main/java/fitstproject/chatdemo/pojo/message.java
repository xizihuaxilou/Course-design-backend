package fitstproject.chatdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class message {
    private Integer id;
    private Integer receiveId;
    private String content;
    private Integer sendId;
    private LocalDateTime createTime;
}
