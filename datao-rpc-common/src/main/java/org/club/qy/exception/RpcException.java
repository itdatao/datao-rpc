package org.club.qy.exception;

import org.club.qy.enums.RpcErrorMessage;

/**
 * @Author hht
 * @Date 2021/5/18 9:03
 */
public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessage rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getErrorMessage());
    }

    public RpcException(RpcErrorMessage rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getErrorMessage() + ": " + detail);
    }
}
