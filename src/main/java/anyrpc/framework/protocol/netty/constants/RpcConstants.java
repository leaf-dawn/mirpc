package anyrpc.framework.protocol.netty.constants;

import sun.misc.Version;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author fzw
 * rpc协议相关
 * @date 2022-08-30 14:49
 */
public class RpcConstants {

    /** 魔数，用于标记协议格式*/
    public static final byte[] MAGIC_NUMBER = {(byte)'g',(byte)'r',(byte)'p',(byte)'c'};
    /** 协议默认字符集 */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    /** 协议版本号 */
    public static final byte VERSION = 1;
    /** 协议总长度 */
    public static final byte TOTAL_LENGTH = 16;
    /** 首部长度 */
    public static final byte HEAD_LENGTH = 16;
    /** 最大帧长 */
    public static final int MAX_FRAME_LENGHT = 8 * 1024 * 1024;
    public class Type {
        //rpc请求类型
        public static final byte REQUEST_TYPE = 1;
        //rpc响应类型
        public static final byte RESPONSE_TYPE = 2;
        //ping
        public static final byte HEARTBEAT_REQUEST_TYPE = 3;
        //pong
        public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    }
}
