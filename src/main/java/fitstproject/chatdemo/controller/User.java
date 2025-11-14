package fitstproject.chatdemo.controller;

import fitstproject.chatdemo.pojo.LogUser;
import fitstproject.chatdemo.pojo.result;
import fitstproject.chatdemo.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequestMapping("/user")
@RestController
public class User {
    @Autowired
    private userService userservice;

    @PostMapping("/login")
    public LogUser log(@RequestBody LogUser loguser) {
        LogUser loginUser = userservice.login(loguser);
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        return loginUser;
    }

    @PostMapping("/register")
    public result register(@RequestBody LogUser loguser) {
        return userservice.register(loguser);
    }

}
