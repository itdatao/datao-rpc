package org.club.qy.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author hht
 * @Date 2021/5/21 10:32
 */
public class ChannelProvider {
    private static final Map<String,Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress socketAddress) {
        String key = socketAddress.toString();
        if (CHANNEL_MAP.containsKey(key)){
            Channel channel = CHANNEL_MAP.get(key);

            return channel!=null&&channel.isActive()?CHANNEL_MAP.get(key):CHANNEL_MAP.remove(key);
        }else{
            return null;
        }

    }

    public void set(InetSocketAddress socketAddress, Channel channel) {
        CHANNEL_MAP.put(socketAddress.toString(),channel);
    }

    public Channel remove(String key){
       return  CHANNEL_MAP.remove(key);
    }
}
