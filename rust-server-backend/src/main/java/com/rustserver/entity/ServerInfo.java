package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_server")
public class ServerInfo {
    @TableId
    private String id;
    private String name;
    private String ip;
    private String port;
    private String steamId;
    private String region;
    private String country;
    private String mapName;
    private String mapSize;
    private String seed;
    private String maxPlayers;
    private String isOfficial;
    private String isModded;
    private String gatherRate;
    private String tags;
    private String website;
    private String discord;
    private String description;
    private String bannerUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
