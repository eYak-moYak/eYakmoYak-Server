package capstone.eYakmoYak.medicine.service;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.repository.UserRepository;
import capstone.eYakmoYak.medicine.domain.Medicine;
import capstone.eYakmoYak.medicine.domain.Prescription;
import capstone.eYakmoYak.medicine.dto.AddMedReq;
import capstone.eYakmoYak.medicine.repository.PrescriptionRepository;
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
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;

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

    public User getUser(String token){
        String username = jwtUtil.getUsername(token);
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new RuntimeException("로그인이 필요한 서비스 입니다.");
        }
        return user;
    }

    public void addMedicine(User user, AddMedReq request){
        Prescription prescription  = Prescription.builder()
                .user(user)
                .start_date(request.getStart_date())
                .end_date(request.getEnd_date())
                .build();

        user.addPrescription(prescription);

        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .dose_time(request.getDose_time())
                .meal_time(request.getMeal_time())
                .build();

        prescription.addMedicine(medicine);

        prescriptionRepository.save(prescription);
    }
}
