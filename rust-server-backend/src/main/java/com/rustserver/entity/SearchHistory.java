package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_search_history")
public class SearchHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private Integer searchCount;
    private LocalDateTime lastSearched;
}
