package capstone.eYakmoYak.auth.contorller;

import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.util.LoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JWTUtil jwtUtil;
    private final LoginUtil utils;

    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestParam String code) {
        // code를 사용하여 토큰 발급 로직 수행
        // 예시: OAuth2 공급자와 통신하여 액세스 토큰과 리프레시 토큰을 받음

        // OAuth2 공급자에서 받은 사용자 정보로 JWT 생성
        String username = "exampleUser"; // 실제 사용자 이름으로 대체
        String access = jwtUtil.createJwt("access", username, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, 86400000L);

        utils.addRefreshEntity(username, refresh, 86400000L);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", access);
        tokens.put("refresh", refresh);

        return ResponseEntity.ok(tokens);
    }
}
