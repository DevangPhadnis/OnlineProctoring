package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user = userRepository.findByUserName(username);
        if(user != null) {
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .build();
        }

        throw new UsernameNotFoundException("User Not Found with user name: " +  username);
    }
}