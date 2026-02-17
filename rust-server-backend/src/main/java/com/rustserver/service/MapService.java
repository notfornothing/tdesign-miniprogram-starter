package com.rustserver.service;

public interface MapService {

    /**
     * Get map image URL for a server
     */
    String getMapUrl(Long serverId);

    /**
     * Get map data for a server
     */
    Object getMapData(Long serverId);
}
