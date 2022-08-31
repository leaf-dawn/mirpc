package anyrpc.framework.protocol.netty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzw
 * rcp协议抽象出来的对象
 * @date 2022-08-30 15:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcMessage<T> {

   private byte messageType;

   private byte codec;

   private byte compress;

    private String requestId;

    private T data;

}
