package org.club.qy.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.club.qy.constants.Constants;
import org.club.qy.entity.RpcMessage;
import org.club.qy.entity.RpcResponse;
import org.club.qy.enums.CompressType;
import org.club.qy.enums.SerializationType;
import org.club.qy.factory.SingletonFactory;

import java.net.InetSocketAddress;

/**
 * @Author hht
 * @Date 2021/5/18 16:59
 */
@Slf4j
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequest unprocessedRequest;
    private final RpcClient rpcClient;

    public RpcClientHandler() {
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
        this.rpcClient = SingletonFactory.getInstance(RpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                //如果是心跳消息只打印日志
                if (messageType == Constants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart response [{}]", tmp.getData());
                } else if (messageType == Constants.RESPONSE_TYPE) {
                    RpcResponse<Object> data = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequest.complete(data);
                }
            }
        } finally {
            //计数器-1
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state==IdleState.WRITER_IDLE){
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                log.info("write idle happen[{}]",socketAddress);
                Channel channel = rpcClient.getChannel(socketAddress);
                RpcMessage message = RpcMessage.builder().codec(SerializationType.PROTOSTUFF.getType())
                        .compressType(CompressType.GZIP.getCode())
                        .messageType(Constants.HEARTBEAT_REQUEST_TYPE)
                        .data(Constants.PING)
                        .build();
                channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }else{
            super.userEventTriggered(ctx,evt);
        }

    }

    //当异常发生时，回调这个方法
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //客户端打印异常
        log.error("client catch exception: ",cause);
        cause.printStackTrace();
        //关闭上下文
        ctx.close();
    }


}
