package com.likelion.server.domain.group.web.controller;

import com.likelion.server.domain.group.service.GroupService;
import com.likelion.server.domain.group.web.dto.CreateGroupRequest;
import com.likelion.server.domain.group.web.dto.CreateGroupResponse;
import com.likelion.server.domain.group.web.dto.GroupJoinRequest;
import com.likelion.server.domain.group.web.dto.GroupJoinResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class GroupController {

    private final GroupService groupService;

    // 새 그룹 생성
    @PostMapping("/group/new")
    public SuccessResponse<CreateGroupResponse> createGroup(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody @Valid CreateGroupRequest createGroupRequest
    ) {
        CreateGroupResponse data = groupService.create(userId, createGroupRequest);
        return SuccessResponse.created(data);
    }

    // 그룹 입장
    @PostMapping("/group/join")
    public SuccessResponse<GroupJoinResponse> joinGroup(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody GroupJoinRequest groupJoinRequest
    ) {
        GroupJoinResponse data = groupService.joinByCode(userId, groupJoinRequest.groupCode());
        return SuccessResponse.ok(data);
    }
}
