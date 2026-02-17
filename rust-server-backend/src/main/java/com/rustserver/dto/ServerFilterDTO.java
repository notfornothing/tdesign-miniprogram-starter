package com.rustserver.dto;

import lombok.Data;

@Data
public class ServerFilterDTO {
    private String keyword;
    private String region;
    private Integer isOfficial;
    private Integer isModded;
    private Integer minPlayers;
    private Integer maxPlayers;
    private String sortField;
    private String sortOrder;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
