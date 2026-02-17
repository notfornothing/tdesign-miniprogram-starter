package com.rustserver.service;

public interface MapService {

    /**
     * Get map image URL for a server
     */
    String getMapUrl(String serverId);

    /**
     * Get map data for a server
     */
    Object getMapData(String serverId);
}
