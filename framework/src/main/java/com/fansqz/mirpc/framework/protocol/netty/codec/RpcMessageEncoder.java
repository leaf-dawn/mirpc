package com.fansqz.mirpc.framework.protocol.netty.codec;

import com.fansqz.mirpc.framework.compress.Compress;
import com.fansqz.mirpc.framework.compress.CompressFactory;
import com.fansqz.mirpc.framework.constants.CompressTypeEnum;
import com.fansqz.mirpc.framework.protocol.netty.constants.RpcConstants;
import com.fansqz.mirpc.framework.protocol.netty.dto.RpcMessage;
import com.fansqz.mirpc.framework.serialize.Serializer;
import com.fansqz.mirpc.framework.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fzw
 * rpc协议编码器
 * 协议格式
 *         4         1           4              1           1       1          4
 * / magic co0de / version / full lenght / messageType / codec / compress / requestId /
 * /                                body                                              /
 * /                        ..........................                                /
 * [migic code :魔法值] [version:协议版本号] [full length 消息长度] [ message type 消息类型]
 * [compress 压缩类型] [ codec: 序列化类型] [requestId 请求id]
 * @date 2022-08-30 10:17
 */

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    /** 原子操作的integer，用于生成自增id */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            byte messageType = msg.getMessageType();
            //留出空位，写消息长度，后面补上
            out.writerIndex(out.writerIndex() + 4);
            out.writeByte(messageType);
            out.writeByte(msg.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            //标记报文总长度
            if (messageType != RpcConstants.MessageType.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.MessageType.HEARTBEAT_RESPONSE_TYPE) {
                //序列化
                Serializer serializer = SerializerFactory.getSerializer(msg.getCodec());
                bodyBytes = serializer.serialize(msg.getData());
                //进行压缩
                Compress compress = CompressFactory.getCompress(msg.getCompress());
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            //移动到报文总长度位置进行写入
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("encode request error !", e);
        }
        
    }
}
