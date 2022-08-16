package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.tcp.protocltcp.MessageProtocol;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author fzw
 * @date 2022-08-16 10:40
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    //每个客户和服务端的handler都是独立的
    private int cout;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();
        //将buffer转成字符串
        String s = new String(content, Charset.forName("utf-8"));
        System.out.println("===============服务端接收到的数据===================");
        System.out.println(len);
        System.out.println(s);
        System.out.println("===========服务端接收到的消息量:"+ (++this.cout) + "====================");
        //服务器会送数据给客户端，回送一个随机id
        ByteBuf byteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString(), Charset.forName("utf-8"));
        ctx.writeAndFlush(byteBuf);
    }
}
