package anyrpc.framework.protocol.netty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzw
 * rcp协议抽象出来的对象
 * @date 2022-08-30 15:26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcMessage {

   private byte messageType;

   private byte codec;

   private byte compress;

    private Integer requestId;

    private Object data;

}
