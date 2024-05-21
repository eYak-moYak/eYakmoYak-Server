package capstone.eYakmoYak.auth.jwt;

import capstone.eYakmoYak.auth.response.AuthenticationErrorCode;
import capstone.eYakmoYak.auth.response.BaseResponse;
import capstone.eYakmoYak.auth.response.BaseResponseStatus;
import capstone.eYakmoYak.auth.response.CustomAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.error("[-] Invalid Token");

            // 오류 내용 기입
            BaseResponse baseResponse = new BaseResponse(BaseResponseStatus.INVALID_JWT);

            Map<String, Object> errorDetails = setErrorDetails(baseResponse);

            sendErrorMessage(response, errorDetails);
        } catch (CustomAuthenticationException e) {
            log.error(e.getMessage());

            if (e.getErrorCode() == AuthenticationErrorCode.EMPTY_AUTHENTICATION) {
                BaseResponse baseResponse = new BaseResponse(BaseResponseStatus.EMPTY_JWT);

                Map<String, Object> errorDetails = setErrorDetails(baseResponse);

                sendErrorMessage(response, errorDetails);
            } else {
                BaseResponse baseResponse = new BaseResponse(BaseResponseStatus.INVALID_JWT);

                Map<String, Object> errorDetails = setErrorDetails(baseResponse);

                sendErrorMessage(response, errorDetails);
            }
        }
    }

    // Set Error Json
    private Map<String, Object> setErrorDetails(BaseResponse baseResponse) {
        Map<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("isSuccess", baseResponse.isSuccess());
        errorDetails.put("code", baseResponse.getCode());
        errorDetails.put("message", baseResponse.getMessage());

        return errorDetails;
    }

    // Send Error Message to Client
    private void sendErrorMessage(HttpServletResponse response, Map<String, Object> errorDetails) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}