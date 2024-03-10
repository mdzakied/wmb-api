package com.enigma.wmb_api.security;

import com.enigma.wmb_api.dto.response.auth.JwtClaims;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Get Barer Token from header http request
            String bearerToken = request.getHeader("Authorization");

            // Conditional Authorized
            if (bearerToken != null && jwtService.verifyJwtToken(bearerToken)) {
                // Create JWT Claims from service
                JwtClaims jwtClaims = jwtService.getClaimsByToken(bearerToken);

                // Create User Account from getByUserId service
                UserAccount userAccount = userAccountService.getByUserId(jwtClaims.getUserAccountId());

                // Create UsernamePasswordAuthenticationToken from request for Authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userAccount.getUsername(),
                        null,
                        userAccount.getAuthorities()
                );

                // Save additional information in authentication detail
                authentication.setDetails(new WebAuthenticationDetails(request));

                // Save to Security Context Holder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Request to controller
        filterChain.doFilter(request, response);
    }
}
