package org.club.qy.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.club.qy.constants.Constants;
import org.club.qy.entity.RpcMessage;
import org.club.qy.entity.RpcRequest;
import org.club.qy.entity.RpcResponse;
import org.club.qy.enums.CompressType;
import org.club.qy.enums.SerializationType;
import org.club.qy.extension.ExtensionLoader;
import org.club.qy.factory.SingletonFactory;
import org.club.qy.netty.RpcRequestTransport;
import org.club.qy.netty.codec.RpcMessageDecoder;
import org.club.qy.netty.codec.RpcMessageEncoder;
import org.club.qy.register.ServiceDiscover;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author hht
 * @Date 2021/5/18 16:57
 */
@Slf4j
public class RpcClient implements RpcRequestTransport {

    private final ServiceDiscover serviceDiscover;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequest unprocessedRequest;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) //Connect_timeout_millis 连接超时时间
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //心跳检测 如果5秒内没有发送数据，客户端就会向服务器发送心跳数据包
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcClientHandler());
                    }
                });


        this.serviceDiscover = ExtensionLoader.getExtensionLoader(ServiceDiscover.class).getExtension("zk");
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //返回值
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();

        //建立连接的channel
        InetSocketAddress inetSocketAddress = serviceDiscover.discoverService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);

        if (channel.isActive()) {
            //添加到未处理的请求集合中
            unprocessedRequest.put(rpcRequest.getRequestId(), completableFuture);
            //构建rpc消息对象 默认序列化方式使用的是PROTOSTUFF

            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationType.KYRO.getType())
                    .compressType(CompressType.GZIP.getCode())
                    .messageType(Constants.REQUEST_TYPE).build();
            //发送消息，异步返回结果
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("send failed:", future.cause());
                }
            });
        } else {

            //还未连接服务端，不能发消息
            throw new IllegalStateException();
        }
        //返回结果

        return completableFuture;
    }

    public Channel getChannel(InetSocketAddress socketAddress) {
        //先从集合中获取，如果没有再建立连接获取，将处于连接状态的channel加到channelProvider
        Channel channel = channelProvider.get(socketAddress);
        //如果没有连接服务端需要先建立连接再发送rpc请求
        if (channel == null) {
            channel = doConnect(socketAddress);
            channelProvider.set(socketAddress, channel);
        }
        return channel;
    }

    @SneakyThrows
    private Channel doConnect(InetSocketAddress socketAddress) {
        CompletableFuture<Channel> channelCompletableFuture = new CompletableFuture<>();
        bootstrap.connect(socketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                channelCompletableFuture.complete(future.channel());
            } else {
                System.out.println("连接状态异常");
                throw new IllegalStateException();
            }
        });
        return channelCompletableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
