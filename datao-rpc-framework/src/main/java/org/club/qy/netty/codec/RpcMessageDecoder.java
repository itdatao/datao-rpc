package org.club.qy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.club.qy.compress.Compress;
import org.club.qy.constants.Constants;
import org.club.qy.entity.RpcMessage;
import org.club.qy.entity.RpcRequest;
import org.club.qy.entity.RpcResponse;
import org.club.qy.enums.CompressType;
import org.club.qy.enums.SerializationType;
import org.club.qy.extension.ExtensionLoader;
import org.club.qy.serialize.Serialization;

import java.util.Arrays;

/**
 * @Author hht
 * @Date 2021/5/18 16:58
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder(){
        this(Constants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,int lengthAdjustment,int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,lengthAdjustment,initialBytesToStrip);
    }

    //解码消息，将字节转换成对象类型
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= Constants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }

        }
        return decoded;

    }

    private Object decodeFrame(ByteBuf frame) {

        checkMagic(frame);
        checkVersion(frame);
        int fullLen = frame.readInt();
        //构建RPC消息体 必须按照顺序读
        byte messageType = frame.readByte();
        byte codecType = frame.readByte();
        byte compressType = frame.readByte();
        int requestId = frame.readInt();
        RpcMessage rpcMessage = RpcMessage.builder().codec(codecType).compressType(compressType).messageType(messageType).requestId(requestId).build();
        if (messageType == Constants.HEARTBEAT_REQUEST_TYPE){
            rpcMessage.setData(Constants.PING);
            return rpcMessage;
        }
        if (messageType == Constants.HEARTBEAT_RESPONSE_TYPE){
            rpcMessage.setData(Constants.PONG);
            return rpcMessage;
        }

        //消息体长度
        int bodyLength = fullLen - Constants.HEAD_LENGTH;
        if (bodyLength>0){
            byte[] body = new byte[bodyLength];
            frame.readBytes(body);
            //如果有消息体，需要对数据进行解压，反序列化，判断消息类型
            String compressName = CompressType.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            body = compress.decompress(body);

            String serializedName = SerializationType.getSerializedName(codecType);
            log.info("codec name :[{}]",codecType);
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(serializedName);

            //判断序列化类型
            if (messageType==Constants.REQUEST_TYPE){
                RpcRequest rpcRequest = serialization.deserialize(body, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            }
            else{
                RpcResponse rpcResponse = serialization.deserialize(body, RpcResponse.class);
                rpcMessage.setData(rpcResponse);
            }
        }

        return rpcMessage;
    }

    //如果发送的消息版本不对抛出异常
    private void checkVersion(ByteBuf in){
        byte version = in.readByte();
        if (version!=Constants.VERSION){
            log.error("version not match. version is {}",version);
            throw new IllegalArgumentException("version not match "+version);
        }

    }
    //检查魔数
    private void checkMagic(ByteBuf in){
        int len = Constants.MAGIC.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i]!=Constants.MAGIC[i]){
                log.error("magic code is error [{}]", Arrays.toString(tmp));
                throw new IllegalArgumentException("magic code error:"+ Arrays.toString(tmp));
            }
        }

    }



}
