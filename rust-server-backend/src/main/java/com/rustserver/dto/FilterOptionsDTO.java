package com.rustserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilterOptionsDTO {
    private List<String> regions;
    private List<GatherRateOption> gatherRates;
    private String totalServers;

    @Data
    public static class GatherRateOption {
        private String label;
        private String min;
        private String max;
    }
}
