package fitstproject.chatdemo.service;

import fitstproject.chatdemo.pojo.LogUser;
import fitstproject.chatdemo.pojo.result;

public interface userService {
    public LogUser login(LogUser loguser);

    result register(LogUser loguser);
}
