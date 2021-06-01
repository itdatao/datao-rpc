package org.club.qy.netty.client;

import org.club.qy.entity.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author hht
 * @Date 2021/5/21 11:04
 */
public class UnprocessedRequest {
    //server端不处理的请求集合
   private final static Map<String,CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURE = new ConcurrentHashMap();

   public void put(String requestId,CompletableFuture<RpcResponse<Object>> future){
       UNPROCESSED_RESPONSE_FUTURE.put(requestId, future);
   }

   public void complete(RpcResponse<Object> rpcResponse){
       CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURE.remove(rpcResponse.getRequestId());
       //删除失败，返回null
       if (future == null) {
           throw new IllegalStateException();
       }else{
           future.complete(rpcResponse);
       }

   }

}
