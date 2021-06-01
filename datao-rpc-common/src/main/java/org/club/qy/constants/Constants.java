package org.club.qy.constants;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author hht
 * @Date 2021/5/18 17:04
 */
public class Constants {
    //常量类
    public static final byte[] MAGIC ={(byte)'m',(byte)'r',(byte)'p',(byte)'c'};
    public static final Charset DEFAULT_CHARSET=UTF_8;

    //心跳消息
    public static final String PING = "ping";
    public static final String PONG = "pong";
    //max frame length
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int MAX_BLOCK_QUEUE_SIZE = 128;

    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    //head length
    public static final int HEAD_LENGTH = 16;
    //default charset
    //version information
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;



}
