package capstone.eYakmoYak.auth.util;

import capstone.eYakmoYak.auth.domain.Refresh;
import capstone.eYakmoYak.auth.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class LoginUtil {

    private final RefreshRepository refreshRepository;

    public Cookie createCookie(String key, String value, int maxAge, String path){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        if (!path.isEmpty()) {
            cookie.setPath(path);
        }
        //cookie.setSecure(true);
        cookie.setHttpOnly(false);

        return cookie;
    }

    public void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs); //만료일자

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
