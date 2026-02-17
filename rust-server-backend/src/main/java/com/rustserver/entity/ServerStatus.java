package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rs_server_status")
public class ServerStatus {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long serverId;
    private Integer players;
    private Integer maxPlayers;
    private Integer queuePlayers;
    private Integer fps;
    private Integer ping;
    private BigDecimal uptime;
    private LocalDateTime recordedAt;
}
