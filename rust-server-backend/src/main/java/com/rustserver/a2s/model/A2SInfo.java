package com.rustserver.a2s.model;

import lombok.Data;

@Data
public class A2SInfo {
    private String address;
    private Integer port;
    private String name;
    private String map;
    private String folder;
    private String game;
    private Integer appId;
    private Integer players;
    private Integer maxPlayers;
    private Integer bots;
    private String serverType;
    private String environment;
    private Integer visibility;
    private Integer vac;
    private String version;
    private Integer protocol;
    private String gameDir;
    private String keywords;
    private Long steamId;
    private Long ping;
}
