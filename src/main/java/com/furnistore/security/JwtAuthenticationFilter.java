package com.furnistore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 [JWT Filter] Auth Header: " + authHeader); // <-- ADD THIS

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ [JWT Filter] No Bearer token found."); // <-- ADD THIS
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);
        System.out.println("🔍 [JWT Filter] Extracted Email: " + userEmail); // <-- ADD THIS

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
            	UserDetails userDetails =
            		    this.userDetailsService.loadUserByUsername(userEmail);

            		if (jwtService.isTokenValid(jwt, userDetails)) {
            		    UsernamePasswordAuthenticationToken authToken =
            		        new UsernamePasswordAuthenticationToken(
            		            userDetails,
            		            null,
            		            userDetails.getAuthorities()
            		        );

            		    SecurityContextHolder.getContext()
            		        .setAuthentication(authToken);
            		}
                 else {
                    System.out.println("❌ [JWT Filter] Token is INVALID (failed isTokenValid check)."); // <-- ADD THIS
                }
            } catch (Exception e) {
                System.err.println("❌ [JWT Filter] Exception: " + e.getMessage()); // <-- ADD THIS
            }
        }

        filterChain.doFilter(request, response);
     }
}