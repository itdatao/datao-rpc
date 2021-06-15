package org.club.qy.loadbalance;

import org.club.qy.entity.RpcRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * @Author hht
 * @Date 2021/6/5 9:17
 */
class PollingLoadBalanceTest {
    public void loadBalance(LoadBalance strategy, int testCount) {

        List<String> configs = Arrays.asList("127.0.0.1:9997", "127.0.0.1:9998", "127.0.0.1:9999","127.0.0.1:9996");
        int[] counts = new int[configs.size()];
        Random random = new Random();
        for (int i = 0; i < testCount; i++) {
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setServiceName("com.serviceImpl"+random.nextInt(100));

            String serviceAddress = strategy.selectServiceAddress(configs, rpcRequest);
            if (serviceAddress.equals(configs.get(0))){
                counts[0]++;
            }
            else if (serviceAddress.equals(configs.get(1))){
                counts[1]++;
            }
            else if (serviceAddress.equals(configs.get(2))){
                counts[2]++;
            }
            else if (serviceAddress.equals(configs.get(3))){
                counts[3]++;
            }

        }

        for (int i = 0; i < configs.size(); i++) {
            System.out.println("serviceAddress:" + configs.get(i)+ "--次数：" + counts[i]);
        }

    }

    @Test
    public void testPollingLoadBalance(){
        loadBalance(new PollingLoadBalance(),100);
    }
}