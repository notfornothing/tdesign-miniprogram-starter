package com.rustserver.controller;

import com.rustserver.common.Result;
import com.rustserver.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * Get map data for a server
     */
    @GetMapping("/servers/{id}/map")
    public Result<Object> getServerMap(@PathVariable String id) {
        Object mapData = mapService.getMapData(id);
        if (mapData == null) {
            return Result.error(404, "Server not found");
        }
        return Result.success(mapData);
    }
}
