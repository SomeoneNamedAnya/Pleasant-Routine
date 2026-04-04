package org.app.user.service;

import org.apache.commons.lang3.ObjectUtils;
import org.app.user.domain.User;
import org.app.user.repository.UserInfoRepository;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    UserInfoRepository userInfoRepository;
    public User getUserInfo(Long id) {

        return userInfoRepository.findById(id).orElse(null);
    }
}
