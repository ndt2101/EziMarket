package com.ndt2101.ezimarket.security;

import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = jwtUtils.getJwtFromRequest(request);
        token.ifPresent(s -> {
            if (jwtUtils.validateToken(s)) {
                UserDetails userPrincipal = null;
                try {
                    userPrincipal = userDetailsService.loadUserByUsername(jwtUtils.getLoginNameFromToken(token.get()));
                } catch (ApplicationException e) {
                    throw new RuntimeException(e);
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal.getUsername(), userPrincipal.getPassword(), userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        });
        filterChain.doFilter(request, response);
    }


}
