package com.macdduck.dram.user.dto;

import com.macdduck.dram.user.entity.User;

public record UserProfileResponse(
        Long id,
        String nickname,
        String email,
        String profileImage,
        Boolean reminderEnabled
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
                user.getReminderEnabled()
        );
    }
}
