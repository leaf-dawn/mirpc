package netty.hartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateEvent;
import sun.awt.util.IdentityLinkedList;

/**
 * @author fzw
 * 心跳处理的事件
 * @date 2022-08-14 8:27
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent) {
            //转型
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType;
            switch(event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + event.state());
            }
            System.out.println(ctx.channel().remoteAddress() + "--超时事件发生--"+eventType );
            System.out.println("服务器做处理");
        }
    }
}
