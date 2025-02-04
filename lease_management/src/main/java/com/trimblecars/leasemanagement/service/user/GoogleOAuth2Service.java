package com.trimblecars.leasemanagement.service.user;


import com.trimblecars.leasemanagement.model.auth.User;
import com.trimblecars.leasemanagement.model.auth.UserRole;
import com.trimblecars.leasemanagement.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleOAuth2Service {

    private final UserRepository userRepository;

    public GoogleOAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuthPostLogin(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            if (email.endsWith("@admin.com")) {
                newUser.setRole(UserRole.ROLE_ADMIN);
            } else if (email.endsWith("@company.com")) {
                newUser.setRole(UserRole.ROLE_OWNER);
            } else {
                newUser.setRole(UserRole.ROLE_CUSTOMER);
            }
            newUser.setPassword(UUID.randomUUID().toString());
            return newUser;
        });
    }
}