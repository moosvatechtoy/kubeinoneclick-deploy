package com.prokarma.oneclick.teams.controller;

import com.prokarma.oneclick.teams.repository.UserRepository;
import com.prokarma.oneclick.teams.Team;
import com.prokarma.oneclick.teams.User;
import com.prokarma.oneclick.teams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    private UserRepository userRepository;

    @Autowired
    public UserControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public User user(Authentication authentication) {
        // in case of anonymous access (like healthcheck)
        if (authentication == null) {
            return null;
        }
        if ("oneclick-backend".equals(authentication.getName())) {
            return null;
        }
        return userRepository.findById(authentication.getName()).orElseThrow();
    }

    @ModelAttribute
    public Team userTeam(Authentication authentication, @ModelAttribute User user) {
        // in case of anonymous access (like healthcheck)
        if (authentication == null) {
            return null;
        }
        // in case of state access only
        if ("oneclick-backend".equals(authentication.getName())) {
            return null;
        }
        return user.getTeam();
    }

}
