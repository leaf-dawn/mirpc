package com.fansqz.mirpc.framework.protocol.dto;

import com.fansqz.mirpc.framework.constants.RpcCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fzw
 * 响应对象
 * @date 2022-08-30 15:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 2732374128347942734L;

    private  String requestId;

    private Integer code;

    private String message;

    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        response.setData(data);
        return response;
    }

}
