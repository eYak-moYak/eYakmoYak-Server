package capstone.eYakmoYak.auth.jwt;

import capstone.eYakmoYak.auth.dto.CustomOAuth2User;
import capstone.eYakmoYak.auth.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String accessToken = getAccessTokenFromHeader(request);
            if(accessToken == null){
                throw new JwtException("Access token is null");
            }

            Claims claims = jwtUtil.validateToken(accessToken);

            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtUtil.getCategory(accessToken);
            if (!category.equals("access")) {
                PrintWriter writer = response.getWriter();
                writer.print("invalid access token");
                response.setStatus(SC_UNAUTHORIZED);
                return;
            }

            // username 값을 획득
            String username = jwtUtil.getUsername(accessToken);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            CustomOAuth2User customUserDetails = new CustomOAuth2User(userDTO);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (JwtException ex){
            logger.info("Failed to authorize/authenticate with JWT due to " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
