package capstone.eYakmoYak.auth.oauth2;

import capstone.eYakmoYak.auth.dto.CustomOAuth2User;
import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.util.LoginUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final LoginUtil utils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        String access = jwtUtil.createJwt("access", username, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, 86400000L);

        utils.addRefreshEntity(username, refresh, 86400000L);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", access);
        tokens.put("refresh", refresh);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), tokens);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/redirect");
    }
}
