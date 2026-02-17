package com.rustserver.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ServerQueryDTO {
    @NotBlank(message = "IP地址不能为空")
    private String ip;

    @NotBlank(message = "端口不能为空")
    private String port;
}
