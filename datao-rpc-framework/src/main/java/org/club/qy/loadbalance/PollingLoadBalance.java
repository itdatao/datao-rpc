package org.club.qy.loadbalance;

import org.club.qy.entity.RpcRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轮询负载均衡算法
 * 实现简单
 * 缺点：和随机一样，无法解决机器性能差异
 * @Author hht
 * @Date 2021/6/5 9:03
 */
public class PollingLoadBalance extends AbstractLoadBalance {
    private final Map<String, Integer> indexMap = new ConcurrentHashMap<>();


    @Override
    protected String select(List<String> serviceAddress, RpcRequest rpcRequest) {
        String serviceName = serviceAddress.get(0);
        Integer index = indexMap.get(serviceName);
        if (index==null){
            indexMap.put(serviceName,0);
            return serviceAddress.get(0);
        }else{
            index++;
            if (index>=serviceAddress.size()){
                //重新从0开始轮询
                index = 0;
            }
            indexMap.put(serviceName,index);
            return serviceAddress.get(index);
        }
    }
}
