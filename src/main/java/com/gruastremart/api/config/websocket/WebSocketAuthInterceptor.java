package com.gruastremart.api.config.websocket;

import com.gruastremart.api.config.security.jwt.JwtTokenProvider;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtFilter;

    public WebSocketAuthInterceptor(JwtTokenProvider jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                Authentication authentication = jwtFilter.getAuthentication(token);
                accessor.setUser(authentication);

                if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATOR"))
                && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                    throw new AccessDeniedException("Access Denied");
                }
            }
        }
        return message;
    }
}