package com.trimblecars.leasemanagement.service.user;


import com.trimblecars.leasemanagement.model.auth.User;
import com.trimblecars.leasemanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService  {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public User findByEmail(String email)  {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    }
}
