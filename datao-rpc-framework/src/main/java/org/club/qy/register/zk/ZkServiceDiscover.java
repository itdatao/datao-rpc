package org.club.qy.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.club.qy.entity.RpcRequest;
import org.club.qy.enums.RpcErrorMessage;
import org.club.qy.exception.RpcException;
import org.club.qy.extension.ExtensionLoader;
import org.club.qy.factory.SingletonFactory;
import org.club.qy.loadbalance.LoadBalance;
import org.club.qy.register.ServiceDiscover;
import org.club.qy.utils.CuratorUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author hht
 * @Date 2021/5/19 9:57
 */
@Slf4j
public class ZkServiceDiscover implements ServiceDiscover {
    private final LoadBalance loadBalance;

    public ZkServiceDiscover() {
        // todo 根据SPI 选择指定负载均衡算法

        this.loadBalance =  ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");

    }

    @Override
    public InetSocketAddress discoverService(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if (childrenNodes==null||childrenNodes.size()==0){
            throw new RpcException(RpcErrorMessage.SERVER_NONE_SERVICE,serviceName);
        }
        String serviceAddress = loadBalance.selectServiceAddress(childrenNodes, rpcRequest);
        log.info("select service address is : {}",serviceAddress);
        String[] strings = serviceAddress.split(":");
        String ip = strings[0];
        Integer port = Integer.parseInt(strings[1]);
        return new InetSocketAddress(ip,port);
    }
}
