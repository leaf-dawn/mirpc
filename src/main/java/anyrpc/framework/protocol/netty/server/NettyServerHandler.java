package anyrpc.framework.protocol.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author fzw
 * @date 2022-08-29 20:27
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端信息
        System.out.println("msg="+msg);
        //客户端调用服务端的api时，需要定义一个协议
        //比如我们要求每次发送消息都必须是以某一个字符串开头“HelloService#hello#” 开头
        if (msg.toString().startsWith("HelloService#hello#")) {
            //截取后面字符串
        }
    }
}
