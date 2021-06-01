package org.club.qy.loadbalance;

import org.club.qy.entity.RpcRequest;

import java.util.List;

/**
 * @Author hht
 * @Date 2021/5/18 10:16
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddress,RpcRequest rpcRequest) {
        //在抽象类中提前判断传入的参数是否满足负载均衡的条件（服务器的数量大于等于2）
        if (serviceAddress == null || serviceAddress.size() == 0) return null;
        if (serviceAddress.size() == 1) return serviceAddress.get(0);
        return select(serviceAddress, rpcRequest);
    }
    //交给子类实现
    protected abstract String select(List<String> serviceAddress, RpcRequest rpcRequest);
}
