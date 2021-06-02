package org.club.qy;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author hht
 * @Date 2021/5/22 17:24
 */
@Slf4j
@RpcScan(basePackage = {"org.club.qy"})
public class ClientMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
