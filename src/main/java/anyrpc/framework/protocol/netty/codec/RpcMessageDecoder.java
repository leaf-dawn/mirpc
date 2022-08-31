package anyrpc.framework.protocol.netty.codec;
import anyrpc.framework.compress.Compress;
import anyrpc.framework.compress.CompressFactory;
import anyrpc.framework.protocol.netty.constants.RpcConstants;
import anyrpc.framework.protocol.netty.dto.RpcMessage;
import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.protocol.netty.dto.RpcResponse;
import anyrpc.framework.serialize.Serializer;
import anyrpc.framework.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

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
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        //最大长度
        //full_length 在 帧开始的5个字节以后
        //full_length 占4字节
        //full_length 靠前9个字符开始读取
        //全部数据都要读取，所以就不跳过了
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      最大帧长，超过最大帧长就抛弃
     * @param lengthFieldOffset   长度字段的偏移量
     * @param lengthFieldLength   长度字段字节数
     * @param lengthAdjustment    全长开始读取的位置的偏移量
     * @param initialBytesToStrip 跳过的字节数
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            //解析出来的必须要大于消息头长度，大于则对里面的data进行解析
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    //解析data
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


    private Object decodeFrame(ByteBuf in) {
        //检测魔数和版本
        checkMagicNumber(in);
        checkVersion(in);
        //读取长度
        int fullLength = in.readInt();
        // 消息类型
        byte messageType = in.readByte();
        //序列化类型
        byte codecType = in.readByte();
        //压缩类型
        byte compressType = in.readByte();
        //请求id
        int requestId = in.readInt();
        //创建消息
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        //如果消息类型是ping
        if (messageType == RpcConstants.MessageType.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData("ping");
            return rpcMessage;
        }
        if (messageType == RpcConstants.MessageType.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData("pong");
            return rpcMessage;
        }
        //消息体长度
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            //先解压
            Compress compress = CompressFactory.getCompress(rpcMessage.getCompress());
            bs = compress.decompress(bs);
            //反序列化
            Serializer serializer = SerializerFactory.getSerializer(rpcMessage.getCodec());
            if (messageType == RpcConstants.MessageType.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}
