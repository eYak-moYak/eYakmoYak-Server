package capstone.eYakmoYak.auth.contorller;

import capstone.eYakmoYak.auth.jwt.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    @Operation(summary = "Access Token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        return jwtService.reissueToken(request, response);
    }
}
