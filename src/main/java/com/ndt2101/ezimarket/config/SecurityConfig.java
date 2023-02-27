package com.ndt2101.ezimarket.config;

import com.ndt2101.ezimarket.security.JwtAuthenticationFilter;
import com.ndt2101.ezimarket.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // TODO: xem lai
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        httpSecurity
                .cors()
                    .configurationSource(request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.applyPermitDefaultValues();
                        configuration.setAllowedMethods(Arrays.asList("DELETE", "PUT", "PATCH"));
                        return configuration;
                    })
                .and()
                .csrf()
                    .disable()
                .authorizeRequests()
                    .antMatchers("/api/auth/**").permitAll()
//                    .antMatchers("/oauth2/**").permitAll()
                .anyRequest()
//                    .authenticated()
                    .permitAll()
                .and()
                .authenticationManager(authenticationManager)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class);
//                .oauth2Login()
//                    .loginPage("/api/auth/login/oauth2")
//                    .userInfoEndpoint()
//                        .userService(oauth2UserService)
//                .and()
//                .successHandler((request, response, authentication) -> {
//                    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//                    log.info(oAuth2User.getEmail());
//                });
        return httpSecurity.build();
    }
}
