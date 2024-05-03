package capstone.eYakmoYak.medicine.service;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.repository.UserRepository;
import capstone.eYakmoYak.medicine.domain.Medicine;
import capstone.eYakmoYak.medicine.domain.Prescription;
import capstone.eYakmoYak.medicine.dto.AddMedReq;
import capstone.eYakmoYak.medicine.dto.AddPreMedReq;
import capstone.eYakmoYak.medicine.dto.AddPreReq;
import capstone.eYakmoYak.medicine.dto.GetMedRes;
import capstone.eYakmoYak.medicine.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 토큰값으로 유저정보 가져오기
     */
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

    public void addPrescription(User user, AddPreReq request){
        Prescription prescription  = Prescription.builder()
                .user(user)
                .pre_name(request.getPre_name())
                .hospital(request.getHospital())
                .pharmacy(request.getPharmacy())
                .pre_date(request.getPre_date())
                .start_date(request.getStart_date())
                .end_date(request.getEnd_date())
                .build();

        user.addPrescription(prescription);

        for(AddPreMedReq medicineRequest : request.getMedicines()){
            Medicine medicine = new Medicine();
            medicine.setName(medicineRequest.getName());
            medicine.setDose_time(medicineRequest.getDose_time());
            medicine.setMeal_time(medicine.getMeal_time());
            prescription.addMedicine(medicine);
        }

        prescriptionRepository.save(prescription);
    }

    public List<GetMedRes> getMedicineList(Long userId) {
        // 유저의 처방전 목록 가져오기
        List<Prescription> prescriptions = prescriptionRepository.findByUser_Id(userId);

        // 모든 처방전의 약 목록 수집
        List<GetMedRes> medList = new ArrayList<>();

        for (Prescription prescription : prescriptions) {
            for (Medicine medicine : prescription.getMedicines()) {
                GetMedRes med = GetMedRes.builder()
                        .name(medicine.getName())
                        .start_date(prescription.getStart_date())
                        .end_date(prescription.getEnd_date())
                        .build();
                medList.add(med);
            }

        }
        return medList;
    }

}
