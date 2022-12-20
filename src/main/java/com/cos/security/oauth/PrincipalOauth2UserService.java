package com.cos.security.oauth;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.auth.oauth.FaceBookUserInfo;
import com.cos.security.auth.oauth.GoogleUserInfo;
import com.cos.security.auth.oauth.NaverUserInfo;
import com.cos.security.auth.oauth.OAuth2UserInfo;
import com.cos.security.config.CustomBCryptPasswordEncoder;
import com.cos.security.domain.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final CustomBCryptPasswordEncoder customBCryptPasswordEncoder;
    private final UserRepository userRepository;

    //후처리된 함수
    //해당 함수가 종료되면 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration());
        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken());
        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //api 종류 필터링
        OAuth2UserInfo userInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            userInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            userInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
           userInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }else{
            System.out.println("저희 사이트는 페이스북,구글,네이버만 지원합니다.");
        }

        //강제 회원가입
        String provider = userInfo.getProvider();
        String providerId = userInfo.getProviderId();
        String username = provider+providerId;
        String email = userInfo.getEmail();
        String password = customBCryptPasswordEncoder.encode("겟인데어");
        String role = "ROLE_USER";
        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
    }
}
