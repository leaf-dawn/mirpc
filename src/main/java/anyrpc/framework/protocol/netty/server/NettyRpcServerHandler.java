package anyrpc.framework.protocol.netty.server;

import anyrpc.framework.constants.CompressTypeEnum;
import anyrpc.framework.constants.SerializationTypeEnum;
import anyrpc.framework.protocol.netty.constants.RpcConstants;
import anyrpc.framework.protocol.netty.dto.RpcMessage;
import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.protocol.netty.dto.RpcResponse;
import anyrpc.framework.utils.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fzw
 * rpc的处理器
 * @date 2022-08-29 20:27
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcServiceHandler rpcServiceHandler;

    public NettyRpcServerHandler() {
        this.rpcServiceHandler = SingletonFactory.getInstance(RpcServiceHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage requestMessage = (RpcMessage) msg;
                byte messageType = requestMessage.getMessageType();
                RpcMessage responseMessage = new RpcMessage();
                responseMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
                responseMessage.setCompress(CompressTypeEnum.GZIP.getCode());

                //ping请求发送pong数据包
                if (RpcConstants.MessageType.HEARTBEAT_REQUEST_TYPE == messageType) {
                    //如果是ping
                    responseMessage.setMessageType(RpcConstants.MessageType.HEARTBEAT_RESPONSE_TYPE);
                    responseMessage.setData("pong");
                    ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    return;
                }

                if (RpcConstants.MessageType.REQUEST_TYPE == messageType) {
                    RpcRequest rpcRequest = (RpcRequest) requestMessage.getData();
                    //执行，添加数据
                    Object result = rpcServiceHandler.handler(rpcRequest);
                    responseMessage.setMessageType(RpcConstants.MessageType.RESPONSE_TYPE);
                    RpcResponse rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                    responseMessage.setData(rpcResponse);
                    //io完成以后，如果操作失败，则关闭channel
                    ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }
        }finally {
            //手动释放msg，返回结果以后，msg不应该继续保留使用
            ReferenceCountUtil.release(msg);
        }
    }
}
