package org.club.qy.provider.impl;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.config.RpcServiceConfig;
import org.club.qy.entity.RpcServiceProperties;
import org.club.qy.enums.RpcErrorMessage;
import org.club.qy.exception.RpcException;
import org.club.qy.extension.ExtensionLoader;
import org.club.qy.netty.server.RpcServer;
import org.club.qy.provider.ServiceProvider;
import org.club.qy.register.ServiceRegister;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author hht
 * @Date 2021/5/20 15:39
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    //RpcService的名称是 接口全路径名+版本+组
    private Map<String, Object> serviceMap; //服务总集合
    //已注册服务的集合
    private Set<String> registeredService;

    private ServiceRegister serviceRegistry;

    public ServiceProviderImpl() {
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegister.class).getExtension("zk");
        serviceMap = new ConcurrentHashMap<>();
        registeredService = new HashSet<>();
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessage.SERVER_NONE_SERVICE);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerServices(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, RpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

}
