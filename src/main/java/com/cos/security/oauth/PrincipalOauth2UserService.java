package com.cos.security.oauth;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.domain.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    //후처리된 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration());
        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken());
        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //강제 회원가입
        String provider = userRequest.getClientRegistration().getClientId();
        String providerId =  oAuth2User.getAttribute("sub");
        String username = provider+providerId;
        String email = oAuth2User.getAttribute("email");
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String role = "ROLE_USER";
        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            userRepository.save(
                    User.builder()
                            .username(username)
                            .password(password)
                            .email(email)
                            .role(role)
                            .provider(provider)
                            .providerId(providerId)
                            .build()
            );
        }

        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
    }
}
