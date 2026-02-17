package com.rustserver.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServerDetailDTO {
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
    private String players;
    private String maxPlayers;
    private String queuePlayers;
    private String fps;
    private String ping;
    private String isOfficial;
    private String isModded;
    private String gatherRate;
    private List<String> tags;
    private String website;
    private String discord;
    private String description;
    private String bannerUrl;
    private String status;
    private String uptime;
    private LocalDateTime lastWipe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
