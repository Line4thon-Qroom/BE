package com.likelion.server.domain.user.web.controller;

import com.likelion.server.domain.user.service.UserService;
import com.likelion.server.domain.user.web.dto.HomeResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/home") //
    public SuccessResponse<HomeResponse> getHome(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        HomeResponse data = userService.getHome(userId);
        return SuccessResponse.ok(data);
    }
}
