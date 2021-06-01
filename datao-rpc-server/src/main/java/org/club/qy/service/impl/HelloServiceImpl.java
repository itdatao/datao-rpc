package org.club.qy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.HelloService;
import org.club.qy.annotation.RpcService;

/**
 * @Author hht
 * @Date 2021/5/22 16:37
 */
@Slf4j
@RpcService(group = "group1",version = "version1")
public class HelloServiceImpl implements HelloService {
    static {
        log.info("HelloServiceImpl 被创建....");
    }

    @Override
    public String sayHello(String message) {

        System.out.println("这是客户端接口传来的参数："+message);
        return message+"123";
    }
}
