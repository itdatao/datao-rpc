package org.club.qy.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.entity.RpcRequest;
import org.club.qy.extension.ExtensionLoader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author hht
 * @Date 2021/5/22 20:53
 */
@Slf4j
class ConsistentHashLoadBalanceTest {

   private LoadBalance loadBalance =  ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");

    @Test
    void select() {

        List<String> list =  new ArrayList<String>(Arrays.asList("127.0.0.1:9997", "127.0.0.1:9998", "127.0.0.1:9999","127.0.0.1:9996"));

        Random random = new Random();
        for (int j = 0; j < 10; j++) {
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setServiceName("com.service.HelloServiceImpl"+random.nextInt(100));
            rpcRequest.setParameters(new Object[]{"1","2","3"});
            rpcRequest.setGroup("2");
            rpcRequest.setVersion("1.0");

            String s = loadBalance.selectServiceAddress(list, rpcRequest);
            System.out.println(rpcRequest.getRpcServiceName()+":"+s);

        }

    }
}