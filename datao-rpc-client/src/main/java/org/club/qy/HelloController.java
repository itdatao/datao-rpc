package org.club.qy;

import org.club.qy.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @Author hht
 * @Date 2021/5/22 17:25
 */
@Component
public class HelloController {
    @RpcReference(version = "version1",group = "group1")
    private HelloService helloService;

    public void test(){
        String s = helloService.sayHello("我是客户端，阿巴阿巴阿巴");
        System.out.println("我是客户端，阿巴阿巴阿巴123".equals(s));
    }
}
