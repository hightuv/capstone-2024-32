package com.example.WebOrder.service;

import com.example.WebOrder.dto.LoginFormDto;
import com.example.WebOrder.dto.UserFormDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username " + username);
        return userRepository.findByUsername(username).get();
    }

    public Boolean isLoginAttemptValid(LoginFormDto dto){
        Optional<User> findUser = userRepository.findByUsername(dto.getUsername());

        if (findUser.isEmpty()) throw new UsernameNotFoundException("유저가 존재하지 않습니다.");

        User user = findUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        return true;
    }

    public Boolean usernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    public Boolean isCurrentUserAuthenticated(Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            return Objects.equals(user.getId(), userId);
        }
        return false;
    }


    public User createUser(UserFormDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setSeats(new ArrayList<>());

        return userRepository.save(user);
    }


}

