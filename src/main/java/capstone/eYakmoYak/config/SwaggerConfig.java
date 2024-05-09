package capstone.eYakmoYak.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "eYakmoYak",
        description = "이미지 인식 기반 복약 관리 및 병용금기 조회 서비스 이약머약 API 명세서",
        version = "1.0"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

}
