package netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author fzw
 * 客户端handler
 * @date 2022-08-13 23:07
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    /** 读取到数据时 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
    }
}
