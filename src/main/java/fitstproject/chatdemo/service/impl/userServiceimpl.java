package fitstproject.chatdemo.service.impl;

import fitstproject.chatdemo.mapper.userMapper;
import fitstproject.chatdemo.pojo.LogUser;
import fitstproject.chatdemo.pojo.result;
import fitstproject.chatdemo.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class userServiceimpl implements userService {
    @Autowired
    userMapper usermapper;

    @Override
    public LogUser login(LogUser loguser) {

        return usermapper.log(loguser);
    }

    @Override
    public result register(LogUser loguser) {
        try {
            usermapper.register(loguser);
            return new result("注册成功", "200", null);
        } catch (Exception e) {
            return new result("注册失败: " + e.getMessage(), "500", null);
        }
    }

}
