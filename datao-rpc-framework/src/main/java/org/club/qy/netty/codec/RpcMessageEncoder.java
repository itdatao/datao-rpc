package org.club.qy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.club.qy.compress.Compress;
import org.club.qy.constants.Constants;
import org.club.qy.entity.RpcMessage;
import org.club.qy.enums.CompressType;
import org.club.qy.enums.SerializationType;
import org.club.qy.extension.ExtensionLoader;
import org.club.qy.serialize.Serialization;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author hht
 * @Date 2021/5/18 16:58
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {


    private static final AtomicInteger ATOMIC_INTEGER  = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf out) throws Exception {

        try {
            out.writeBytes(Constants.MAGIC);
            out.writeByte(Constants.VERSION);
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressType.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // build full length
            byte[] bodyBytes = null;
            int fullLength = Constants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != Constants.HEARTBEAT_REQUEST_TYPE
                    && messageType != Constants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                String codecName = SerializationType.getSerializedName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serialization serializer = ExtensionLoader.getExtensionLoader(Serialization.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // compress the bytes
                String compressName = CompressType.getName(rpcMessage.getCompressType());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + Constants.MAGIC.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);

        }catch (Exception e){
            log.error("encode process error...{}",e.getMessage());
        }

    }
}
