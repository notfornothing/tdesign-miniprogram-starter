package com.rustserver.service;

import com.rustserver.a2s.model.A2SInfo;
import com.rustserver.a2s.model.A2SPlayer;
import com.rustserver.common.PageResult;
import com.rustserver.dto.*;
import com.rustserver.entity.ServerInfo;

import java.util.List;

public interface ServerService {

    /**
     * Get server list with pagination and filters
     */
    PageResult<ServerListDTO> getServerList(ServerFilterDTO filter);

    /**
     * Get server detail by ID
     */
    ServerDetailDTO getServerDetail(String id);

    /**
     * Query server using A2S protocol
     */
    A2SInfo queryServer(String ip, String port);

    /**
     * Get online players of a server
     */
    List<A2SPlayer> getOnlinePlayers(String serverId);

    /**
     * Query online players directly using A2S
     */
    List<A2SPlayer> queryPlayers(String ip, String port);

    /**
     * Add a new server
     */
    ServerInfo addServer(ServerQueryDTO dto);

    /**
     * Delete a server
     */
    void deleteServer(String id);

    /**
     * Refresh server status
     */
    void refreshServerStatus(String serverId);

    /**
     * Get filter options
     */
    FilterOptionsDTO getFilterOptions();
}
