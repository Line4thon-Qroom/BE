package com.likelion.server.domain.group.service;

import com.likelion.server.domain.group.web.dto.CreateGroupRequest;
import com.likelion.server.domain.group.web.dto.CreateGroupResponse;

public interface GroupService {
    CreateGroupResponse create(Long userId, CreateGroupRequest req);
}
