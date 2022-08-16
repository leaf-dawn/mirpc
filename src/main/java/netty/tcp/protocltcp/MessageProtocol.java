package netty.tcp.protocltcp;

import lombok.Data;

/**
 * @author nbzw
 * @date 2022-08-16 11:14
 */

//协议包
@Data
public class MessageProtocol {

    private int len;  //长度
    private byte[] content;
}
