package org.club.qy.config;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.netty.server.RpcServer;
import org.club.qy.utils.CuratorUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @Author hht
 * @Date 2021/5/20 11:34
 */
@Slf4j
public class ShutdownHook {
    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return SHUTDOWN_HOOK;
    }

    public static void clearAll() {
        log.info("clear all register.......");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        //清除注册中心中的已创建的节点
                        CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcServer.PORT));
                    } catch (UnknownHostException e) {
                        log.error("clear register fail:{}", e.getMessage());
                    }
                    // threadPoll shutdown
            })
        );
    }
}
