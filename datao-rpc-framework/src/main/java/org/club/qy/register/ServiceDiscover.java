package org.club.qy.register;

import org.club.qy.entity.RpcRequest;
import org.club.qy.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Author hht
 * @Date 2021/5/18 17:00
 */
@SPI
public interface ServiceDiscover {
    //服务发现
    InetSocketAddress discoverService(RpcRequest rpcRequest);
}
