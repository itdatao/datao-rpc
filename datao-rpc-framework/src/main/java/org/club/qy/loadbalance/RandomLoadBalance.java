package org.club.qy.loadbalance;

import org.club.qy.entity.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @Author hht
 * @Date 2021/5/18 9:58
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String select(List<String> serviceAddress, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
