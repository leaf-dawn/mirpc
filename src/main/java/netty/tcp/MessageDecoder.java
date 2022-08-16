package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty.tcp.protocltcp.MessageProtocol;

import java.util.List;

/**
 * @author fzw
 * 解码
 * @date 2022-08-16 11:41
 */
public class MessageDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("messageDecoder 方法被调用");
        //将二进制字节码转为messageProtocol
        int length = in.readInt();

        byte[] content = new byte[length];
        in.readBytes(content);
        //封装成MessageProtocol对象放入out
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(length);
        messageProtocol.setContent(content);
        out.add(messageProtocol);
    }
}
