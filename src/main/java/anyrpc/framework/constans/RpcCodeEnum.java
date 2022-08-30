package anyrpc.framework.constans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author fzw
 * rpc相关响应码
 * @date 2022-08-30 15:58
 */
@Getter
@AllArgsConstructor
public enum  RpcCodeEnum {
    /** 响应码 */
    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    private final int code;
    private final String message;
}
