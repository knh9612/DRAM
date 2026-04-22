package com.macdduck.dram.user.controller;

import com.macdduck.dram.user.dto.UserProfileResponse;
import com.macdduck.dram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserProfileResponse getProfile(@PathVariable Long userId) {
        return userService.getProfile(userId);
    }

    @PatchMapping("/{userId}/reminder")
    public void updateReminderEnabled(@PathVariable Long userId, @RequestParam Boolean reminderEnabled) {
        userService.updateReminderEnabled(userId, reminderEnabled);
    }
}
