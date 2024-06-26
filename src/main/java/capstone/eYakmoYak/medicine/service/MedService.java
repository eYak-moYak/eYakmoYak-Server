package capstone.eYakmoYak.medicine.service;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.auth.jwt.JWTUtil;
import capstone.eYakmoYak.auth.repository.UserRepository;
import capstone.eYakmoYak.medicine.domain.Contraindication;
import capstone.eYakmoYak.medicine.domain.Medicine;
import capstone.eYakmoYak.medicine.domain.Prescription;
import capstone.eYakmoYak.medicine.dto.*;
import capstone.eYakmoYak.medicine.repository.MedicineRepository;
import capstone.eYakmoYak.medicine.repository.PrescriptionRepository;
import capstone.eYakmoYak.medicine.repository.ContraindicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MedService {
    private final WebClient webClient;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final ContraindicationRepository contraindicationRepository;
    private final S3Service s3Service;

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

    public Long addMedicine(User user, AddMedReq request) throws IOException {
        Prescription prescription  = Prescription.builder()
                .user(user)
                .start_date(request.getStart_date())
                .end_date(request.getEnd_date())
                .build();

        user.addPrescription(prescription);

        // S3에 이미지 업로드
        String name = request.getName();
        String imgUrl = request.getImgUrl();
        String s3ImageUrl = getS3UrlForMedicine(name, imgUrl);

        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .dose_time(request.getDose_time())
                .meal_time(request.getMeal_time())
                .imgUrl(s3ImageUrl)
                .build();

        prescription.addMedicine(medicine);

        prescriptionRepository.save(prescription);
        return prescription.getId();
    }

    public Long addPrescription(User user, AddPreReq request) throws IOException {
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

            String name = medicineRequest.getName();
            String imgUrl = medicineRequest.getImgUrl();
            String s3ImageUrl = getS3UrlForMedicine(name, imgUrl);

            Medicine medicine = new Medicine();
            medicine.setName(medicineRequest.getName());
            medicine.setDose_time(medicineRequest.getDose_time());
            medicine.setMeal_time(medicine.getMeal_time());
            medicine.setImgUrl(s3ImageUrl);
            prescription.addMedicine(medicine);
        }

        prescriptionRepository.save(prescription);
        return prescription.getId();
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
                        .imgUrl(medicine.getImgUrl())
                        .build();
                medList.add(med);
            }

        }
        return medList;
    }

    public List<GetContRes> getContList(List<String> medicines, String name) {
        // 선택한 약품 리스트, 조회할 약품명 가져오기
        name = strProcess(name);

        List<GetContRes> contList = new ArrayList<>();

        List<Contraindication> contListA = contraindicationRepository.findByMedAContaining(name);
        for (Contraindication cont : contListA) {
            for (String med : medicines) {
                if (cont.getMedB().contains(strProcess(med))) {
                    GetContRes res = GetContRes.builder()
                            .name(cont.getMedB())
                            .reason(cont.getReason())
                            .build();
                    contList.add(res);
                }
            }
        }

        List<Contraindication> contListB = contraindicationRepository.findByMedBContaining(name);
        for (Contraindication cont : contListB) {
            for (String med : medicines) {
                if (cont.getMedA().contains(strProcess(med))) {
                    GetContRes res = GetContRes.builder()
                            .name(cont.getMedA())
                            .reason(cont.getReason())
                            .build();
                    contList.add(res);
                }
            }
        }

        return contList;
    }

    public String getS3UrlForMedicine(String name, String imgUrl) throws IOException {

        List<Medicine> existingMedicine = medicineRepository.findByName(name);

        if (!existingMedicine.isEmpty()) {
            // 동일한 이름의 약이 존재하면 해당 S3 URL 재사용
            return existingMedicine.get(0).getImgUrl();
        }

        // 이미지 URL이 유효한 경우에만 S3에 업로드
        if (imgUrl != null && !imgUrl.isEmpty()) {
            return s3Service.uploadImage(imgUrl, "medicine-images");  // S3에 업로드 및 URL 반환
        }

        return "No Image";  // 이미지가 없는 경우 기본값
    }
    public GetInfoList getUserPreAndMed(Long userId){
        List<Prescription> prescriptions = prescriptionRepository.findByUser_Id(userId);

        List<GetMedList> medList = new ArrayList<>();
        List<GetPreList> preList = new ArrayList<>();

        for(Prescription prescription : prescriptions){
            if(prescription.getHospital() == null){
                // 병원명이 null인 경우
                for(Medicine medicine : prescription.getMedicines()){
                    GetMedList med = GetMedList.builder()
                            .pre_id(prescription.getId())
                            .name(medicine.getName())
                            .start_date(prescription.getStart_date())
                            .end_date(prescription.getEnd_date())
                            .dose_time(medicine.getDose_time())
                            .meal_time(medicine.getMeal_time())
                    .build();
                    medList.add(med);
                }
            } else {
                // 병원명이 있는 경우 처방전 목록을 추가
                GetPreList pre = GetPreList.builder()
                        .pre_id(prescription.getId())
                        .pre_name(prescription.getPre_name())
                        .pre_date(prescription.getPre_date())
                        .hospital(prescription.getHospital())
                        .pharmacy(prescription.getPharmacy())
                        .countMedicine(prescription.getMedicines().size())
                        .build();
                preList.add(pre);
            }
        }
        GetInfoList getInfoList = GetInfoList.builder()
                .medicines(medList)
                .prescriptions(preList)
                .build();

        return getInfoList;
    }

    public GetPrescription getPrescription(Long preId){
        Prescription prescription = prescriptionRepository.findById(preId).get();

        List<GetMedList> medList = new ArrayList<>();

        for(Medicine medicine : prescription.getMedicines()){
            GetMedList med = GetMedList.builder()
                    .name(medicine.getName())
                    .start_date(prescription.getStart_date())
                    .end_date(prescription.getEnd_date())
                    .dose_time(medicine.getDose_time())
                    .meal_time(medicine.getMeal_time())
                    .build();
            medList.add(med);
        }

        GetPrescription getPreMedList = GetPrescription.builder()
                .pre_name(prescription.getPre_name())
                .pre_date(prescription.getPre_date())
                .hospital(prescription.getHospital())
                .pharmacy(prescription.getPharmacy())
                .medicines(medList)
                .build();

        return getPreMedList;
    }

    public String strProcess(String name) {
        String strNumber = FindFirstNumber(name);
        if (strNumber != null) {
            name = strNumber;
        }
        String strParen = FindParen(name);
        if (strParen != null) {
            name = strParen;
        }
        return name;
    }

    public String FindFirstNumber(String name) {
        Pattern pattern = Pattern.compile("[/\\d]+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return name.substring(0, matcher.end());
        }
        else {
            return null;
        }
    }
    public String FindParen(String name) {
        int parenIdx = name.indexOf('(');
        if (parenIdx == -1) {
            return null;
        }
        else {
            return name.substring(0, parenIdx);
        }
    }
}
