package com.rustserver.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ServerQueryDTO {
    @NotBlank(message = "IP地址不能为空")
    private String ip;

    @NotNull(message = "端口不能为空")
    private Integer port;
}
