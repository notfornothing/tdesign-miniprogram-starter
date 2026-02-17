package com.rustserver.dto;

import lombok.Data;

@Data
public class ServerListDTO {
    private String id;
    private String name;
    private String ip;
    private String port;
    private String region;
    private String country;
    private String mapName;
    private String mapSize;
    private String players;
    private String maxPlayers;
    private String queuePlayers;
    private String ping;
    private String isOfficial;
    private String isModded;
    private String gatherRate;
    private String status;
    private String bannerUrl;
}
