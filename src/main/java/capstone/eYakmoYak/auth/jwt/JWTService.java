package capstone.eYakmoYak.auth.jwt;

import capstone.eYakmoYak.auth.repository.RefreshRepository;
import capstone.eYakmoYak.auth.util.LoginUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final JWTUtil jwtUtil;
    private final LoginUtil loginUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refresh = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("Refresh token is null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);

        String newAccess = jwtUtil.createJwt("access", username, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, 86400000L);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(loginUtil.createCookie("refresh", newRefresh, 24*60*60, ""));

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        loginUtil.addRefreshEntity(username, newRefresh, 86400000L);

        System.out.println("newAccess = " + newAccess);
        System.out.println("newRefresh = " + newRefresh);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
