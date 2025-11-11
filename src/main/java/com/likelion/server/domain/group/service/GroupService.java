package com.likelion.server.domain.group.service;

import com.likelion.server.domain.group.web.dto.CreateGroupRequest;
import com.likelion.server.domain.group.web.dto.CreateGroupResponse;
import com.likelion.server.domain.group.web.dto.GroupJoinResponse;

public interface GroupService {
    CreateGroupResponse create(Long userId, CreateGroupRequest req);
    GroupJoinResponse joinByCode(Long userId, String groupCode);
    void leaveGroup(Long userId, Long groupId);
}
