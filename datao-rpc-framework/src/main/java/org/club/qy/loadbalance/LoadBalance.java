package org.club.qy.loadbalance;

import org.club.qy.entity.RpcRequest;
import org.club.qy.extension.SPI;

import java.util.List;

/**
 * @Author hht
 * @Date 2021/5/18 10:13
 */
@SPI
public interface LoadBalance {
    /**
     *
     * @param serviceAddress 多台机器的调用地址
     * @param rpcRequest rpc请求
     * @return 选择一台机器的调用地址
     */
    String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest);
}
