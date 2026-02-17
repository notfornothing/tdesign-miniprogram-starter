package com.rustserver.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SERVER_NOT_FOUND(404, "服务器不存在"),
    PLAYER_NOT_FOUND(404, "玩家不存在"),
    QUERY_FAILED(500, "服务器查询失败"),
    INVALID_PARAMETER(400, "参数错误"),
    NETWORK_ERROR(503, "网络连接失败"),
    TIMEOUT(504, "查询超时");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
