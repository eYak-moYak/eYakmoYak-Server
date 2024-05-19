package capstone.eYakmoYak.auth.oauth2;

import capstone.eYakmoYak.auth.dto.CustomOAuth2User;
import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.util.LoginUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    private final LoginUtil utils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 유저 정보
        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, 86400000L);

        //Refresh 토큰 저장
        utils.addRefreshEntity(username, refresh, 86400000L);

        response.setHeader("access", access);
        Cookie refreshToken = utils.createCookie("refresh", refresh, 60*60*60*60, "/");
        refreshToken.setSecure(true);
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);

        String cookieHeader = String.format("%s=%s; Max-Age=%d; Path=%s; Secure; HttpOnly; SameSite=None",
                refreshToken.getName(),
                refreshToken.getValue(),
                refreshToken.getMaxAge(),
                refreshToken.getPath());
        response.setHeader("Set-Cookie", cookieHeader);

        response.setStatus(HttpStatus.OK.value());

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/redirect?access=" + access);
//        System.out.println("refresh = " + refresh);
//        System.out.println("access = " + access);
    }


}
