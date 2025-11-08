package com.likelion.server.domain.group.web.dto;

public record CreateGroupResponse(
        Long id,
        String groupName,
        String examDate,   // yyyy-MM-dd or null
        String groupCode,
        Integer imageNum
) { }
