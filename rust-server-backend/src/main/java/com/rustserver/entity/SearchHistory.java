package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_search_history")
public class SearchHistory {
    @TableId
    private String id;
    private String keyword;
    private String searchCount;
    private LocalDateTime lastSearched;
}
