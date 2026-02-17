package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_server_status")
public class ServerStatus {
    @TableId
    private String id;
    private String serverId;
    private String players;
    private String maxPlayers;
    private String queuePlayers;
    private String fps;
    private String ping;
    private String uptime;
    private LocalDateTime recordedAt;
}
