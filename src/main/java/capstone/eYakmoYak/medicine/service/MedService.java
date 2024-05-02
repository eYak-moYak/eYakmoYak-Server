package capstone.eYakmoYak.medicine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class MedService {

    private final WebClient webClient;

    @Value("${external.api.service-key}")
    private String serviceKey;

    public String getMedInfo(String itemName) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("pageNo", 1)
                                .queryParam("numOfRows", 10)
                                .queryParam("type", "json");
                        if (itemName != null && !itemName.isEmpty()) {
                            try {
                                uriBuilder.queryParam("itemName", URLEncoder.encode(itemName, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;
        } catch (WebClientResponseException e) {
            return "Error: " + e.getRawStatusCode() + " - " + e.getResponseBodyAsString();
        }
    }
}
