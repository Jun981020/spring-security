package com.cos.security.config;

import com.cos.security.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.logging.Filter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePassword(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
    }
    /**
     * Creates the Spring Security Filter Chain
     * @return the {@link Filter} that represents the security filter chain
     * @throws Exception
     */
//    @Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
//    public Filter springSecurityFilterChain() throws Exception {
//        XMLStreamWriterImpl.Element webSecurityConfigurers;
//        boolean hasConfigurers = webSecurityConfigurers != null
//                && !webSecurityConfigurers.isEmpty();
//        if (!hasConfigurers) {
//            Jlink objectObjectPostProcessor;
//            WebSecurityConfigurerAdapter adapter = objectObjectPostProcessor
//                    .postProcess(new WebSecurityConfigurerAdapter() {
//                    });
//            webSecurity.apply(adapter);
//        }
//        return webSecurity.build();
//    }
}
