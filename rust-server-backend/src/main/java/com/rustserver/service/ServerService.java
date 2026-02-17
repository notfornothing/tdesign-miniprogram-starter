package com.rustserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    ServerDetailDTO getServerDetail(Long id);

    /**
     * Query server using A2S protocol
     */
    A2SInfo queryServer(String ip, Integer port);

    /**
     * Get online players of a server
     */
    List<A2SPlayer> getOnlinePlayers(Long serverId);

    /**
     * Query online players directly using A2S
     */
    List<A2SPlayer> queryPlayers(String ip, Integer port);

    /**
     * Add a new server
     */
    ServerInfo addServer(ServerQueryDTO dto);

    /**
     * Delete a server
     */
    void deleteServer(Long id);

    /**
     * Refresh server status
     */
    void refreshServerStatus(Long serverId);

    /**
     * Get filter options
     */
    FilterOptionsDTO getFilterOptions();
}
