package org.club.qy.handler;

import lombok.extern.slf4j.Slf4j;
import org.club.qy.entity.RpcRequest;
import org.club.qy.factory.SingletonFactory;
import org.club.qy.provider.ServiceProvider;
import org.club.qy.provider.impl.ServiceProviderImpl;

import java.lang.reflect.Method;

/**
 * @Author hht
 * @Date 2021/5/18 17:00
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider ;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest){
        Object result = null;
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service,rpcRequest.getParameters());
            log.info("服务接口执行成功：service：【{}】,result:【{}】",rpcRequest.getServiceName(),result);
        }catch (Exception e){
            log.error(e.getMessage());
        }


        return result;
    }


}
