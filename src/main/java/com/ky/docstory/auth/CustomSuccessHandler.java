package com.ky.docstory.auth;

import com.ky.docstory.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Value("${app.redirect-url}")
    private String redirectUri;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String providerId = customOAuth2User.getName();

        Map<String, Object> claims = new HashMap<>();
        claims.put("providerId", providerId);

        String token = jwtUtil.createToken(claims);
        Cookie jwtCookie = jwtUtil.createJwtCookie(token);

        response.addCookie(jwtCookie);
        response.sendRedirect(redirectUri);
    }
}
