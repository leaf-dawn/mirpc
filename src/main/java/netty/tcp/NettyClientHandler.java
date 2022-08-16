package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.tcp.protocltcp.MessageProtocol;
import sun.plugin2.message.Message;

import java.nio.charset.Charset;

/**
 * @author fzw
 * @date 2022-08-16 10:25
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //使用客户端发送10条数据
        //发送10条数据
        for (int i = 0;i < 10; i++) {
            String meg = "今天天气冷，吃火锅";
            byte[] content = meg.getBytes(Charset.forName("utf-8"));
            int length = meg.getBytes(Charset.forName("utf-8")).length;
            //创建协议对象
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setLen(length);
            messageProtocol.setContent(content);
            ctx.writeAndFlush(messageProtocol);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {


        String message = new String(msg.getContent(),Charset.forName("utf-8"));
        System.out.println("客户端接收到的消息=" + message);
        System.out.println("客户端接收消息数量=" + (++this.count));
    }
}
