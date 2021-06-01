package org.club.qy.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.club.qy.constants.Constants;
import org.club.qy.entity.RpcMessage;
import org.club.qy.entity.RpcRequest;
import org.club.qy.entity.RpcResponse;
import org.club.qy.enums.CompressType;
import org.club.qy.enums.RpcResponseCode;
import org.club.qy.enums.SerializationType;
import org.club.qy.factory.SingletonFactory;
import org.club.qy.handler.RpcRequestHandler;


/**
 * @Author hht
 * @Date 2021/5/18 16:58
 */
@Slf4j
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public RpcServerHandler(){
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage){
                log.info("server receive msg=[{}]",msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationType.PROTOSTUFF.getType());
                rpcMessage.setCompressType(CompressType.GZIP.getCode());
                if (messageType== Constants.HEARTBEAT_REQUEST_TYPE){
                    rpcMessage.setMessageType(Constants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(Constants.PONG);
                }else{
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(Constants.RESPONSE_TYPE);
                    if (ctx.channel().isActive()&&ctx.channel().isWritable()){
                        RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(response);
                    }else{
                        RpcResponse<Object> rpcResponse = RpcResponse.failure(RpcResponseCode.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }

                    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

                }
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state==IdleState.READER_IDLE){
                log.info("idle happen,so close connect");
                ctx.close();
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("server catch exception ",cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
