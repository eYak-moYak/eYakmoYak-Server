package capstone.eYakmoYak.auth.contorller;

import capstone.eYakmoYak.auth.jwt.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Auth")
@Controller
@ResponseBody
@RequiredArgsConstructor
public class AuthController {

    private final JWTService jwtService;

    /**
     * accessToken 재발급
     */
    @Operation(summary = "Access Token 재발급", description = "Cookie에 refresh=토큰값 으로 넣어주면 Access Token을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        return jwtService.reissueToken(request, response);
    }

    /**
     * refresh Token 전달
     */
    @Operation(summary = "refresh Token", description = "refresh Token을 전달합니다.")
    @GetMapping("/check-refresh-token")
    public String checkRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "NO refresh token found";
    }
}
