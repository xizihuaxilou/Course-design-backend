package fitstproject.chatdemo.Aop;

import fitstproject.chatdemo.pojo.result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// @Aspect // 暂时禁用AOP,避免返回值类型冲突
public class aspect {
    // @Around("execution(* fitstproject.chatdemo.controller.*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        result result = new result();
        Object r = joinPoint.proceed();
        result.setData(r);
        result.setMessage("成功");
        result.setCode("200");

        return result;
    }
}
