package com.likelion.server.domain.group.web.controller;

import com.likelion.server.domain.group.service.GroupService;
import com.likelion.server.domain.group.web.dto.*;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 그룹 퇴장(MEMBER)
    @DeleteMapping("/group/{group_id}/leave")
    public SuccessResponse<Void> leaveGroup(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("group_id") Long groupId
    ) {
        groupService.leaveGroup(userId, groupId);
        return SuccessResponse.ok(null);
    }

    // 그룹 삭제(LEADER)
    @DeleteMapping("/group/{group_id}")
    public SuccessResponse<Void> deleteGroup(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("group_id") Long groupId
    ) {
        groupService.deleteGroup(userId, groupId);
        return SuccessResponse.ok(null);
    }

    // 그룹 홈화면 조회
    @GetMapping("/group/{group_id}/main") // [cite: image_ae2c3e.png]
    public SuccessResponse<GroupMainResponse> getGroupMain(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("group_id") Long groupId
    ) {
        GroupMainResponse data = groupService.getGroupMain(userId, groupId);
        return SuccessResponse.ok(data);
    }
}
