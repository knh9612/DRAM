package com.macdduck.dram.user.service;

import com.macdduck.dram.global.exception.BusinessException;
import com.macdduck.dram.global.exception.ErrorCode;
import com.macdduck.dram.user.dto.UserProfileResponse;
import com.macdduck.dram.user.entity.User;
import com.macdduck.dram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public void updateReminderEnabled(Long userId, Boolean reminderEnabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateReminderEnabled(reminderEnabled);
    }
}
