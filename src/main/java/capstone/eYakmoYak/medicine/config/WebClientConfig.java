package capstone.eYakmoYak.medicine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
                .build();
    }
}
