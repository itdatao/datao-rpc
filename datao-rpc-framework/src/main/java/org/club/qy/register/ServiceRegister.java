package org.club.qy.register;

import org.club.qy.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Author hht
 * @Date 2021/5/18 17:00
 */
@SPI
public interface ServiceRegister {
    //服务注册
    void registerServices(String serviceName, InetSocketAddress inetSocketAddress);
}
