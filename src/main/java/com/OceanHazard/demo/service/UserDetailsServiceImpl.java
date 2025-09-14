package com.OceanHazard.demo.service;


import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user=userRepository.findByUsername(username);
        if(user!=null){
            UserDetails userDetails=org.springframework.security.core.userdetails.User.builder()
                    .username(user.username)
                    .password(user.password)
                    .roles(user.roles.toArray(new String[user.roles.size()]))
                    .build();
            return userDetails;
        }
        return null;
    }
}
