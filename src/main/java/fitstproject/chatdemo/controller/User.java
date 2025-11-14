package fitstproject.chatdemo.controller;

import fitstproject.chatdemo.pojo.LogUser;
import fitstproject.chatdemo.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/user")
@RestController
public class User {
    @Autowired
    private userService userservice;

    @PostMapping("/login")
    public Map<String, Object> log(@RequestBody LogUser loguser) {
        LogUser loginUser = userservice.login(loguser);
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("token", UUID.randomUUID().toString());
        result.put("userId", loginUser.getId());
        result.put("username", loginUser.getUsername());
        return result;
    }

    @PostMapping("/register")
    public Object register(@RequestBody LogUser loguser) {
        return userservice.register(loguser);
    }

}
