package org.club.qy.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.extension.ExtensionLoader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        AtomicInteger atomicInteger = new AtomicInteger(0);
        /*for (int j = 0; j < 4; j++) {
            int i = atomicInteger.getAndIncrement();
            String s = loadBalance.selectServiceAddress(list, "server" + i);
            System.out.println(s);

        }*/

    }
}