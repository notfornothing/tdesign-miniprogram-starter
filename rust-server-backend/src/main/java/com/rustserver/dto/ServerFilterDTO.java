package com.rustserver.dto;

import lombok.Data;

@Data
public class ServerFilterDTO {
    private String keyword;
    private String region;
    private String isOfficial;
    private String isModded;
    private String minPlayers;
    private String maxPlayers;
    private String sortField;
    private String sortOrder;
    private String pageNum = "1";
    private String pageSize = "20";
}
