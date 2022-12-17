package com.cos.security.controller;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.domain.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class indexController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @GetMapping({"","/"})
    public String index(){
        return "index";
    }
    @GetMapping("/login/test")
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("test login ================");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication.getPrincipal() = " + principal.getUser());
        System.out.println("userDetails = " + userDetails.getUser());
        return "세션정보 확인";
    }
    @GetMapping("/user")
    public @ResponseBody String user(){
        return "user";
    }
    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }
    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }
    @GetMapping("/loginForm")
    public String login(){
        return "loginForm";
    }
    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }
    @PostMapping("/join")
    public String join(User user){
        user.setRole("ROLE_USER");
        user.setCreateDate(LocalDateTime.now());
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user);
        userRepository.save(user);
        return "redirect:/loginForm";
    }
    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "data";
    }
}
