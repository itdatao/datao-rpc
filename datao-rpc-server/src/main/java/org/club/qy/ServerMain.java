package org.club.qy;

import org.club.qy.annotation.RpcScan;
import org.club.qy.config.RpcServiceConfig;
import org.club.qy.entity.RpcServiceProperties;
import org.club.qy.netty.server.RpcServer;
import org.club.qy.service.impl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//扫描的包
@RpcScan(basePackage = {"org.club.qy"})
public class ServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServerMain.class);
        RpcServer rpcServer = (RpcServer) applicationContext.getBean("rpcServer");
        //创建被调用的接口
        HelloService helloService = new HelloServiceImpl();
        //注册服务并启动
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(helloService).build();
        rpcServer.registerService(rpcServiceConfig);
        rpcServer.start();
    }
}
