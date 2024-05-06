package capstone.eYakmoYak.medicine.controller;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.medicine.dto.AddMedReq;
import capstone.eYakmoYak.medicine.dto.AddPreReq;
import capstone.eYakmoYak.medicine.dto.GetMedRes;
import capstone.eYakmoYak.medicine.service.MedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Medicine", description = "복약 API")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MedController {

    private final MedService medService;

    /**
     * 약품상세정보 조회
     */
    @Operation(summary = "약품 상세 정보 조회", description = "복용 중인 약품의 정보를 조회합니다.")
    @GetMapping("/get/medicineInfo")
    public ResponseEntity<?> getMedInfo(@RequestParam(value = "itemName", required = false) String itemName){
        String medInfo = medService.getMedInfo(itemName);
        return ResponseEntity.ok(medInfo);
    }

    /**
     * 개별약 등록
     */
    @Operation(summary = "개별약 등록", description = "복용하는 개별 약을 등록합니다.")
    @PostMapping("/add/medicine")
    public ResponseEntity<?> addMedicine(@RequestHeader("access") String token, @RequestBody AddMedReq request) throws IOException {
        User user = medService.getUser(token);
        medService.addMedicine(user, request);

        return ResponseEntity.ok("Medicine created successfully");
    }

    /**
     * 처방약 등록
     */
    @Operation(summary = "처방약 등록", description = "처방 받은 약의 리스트를 등록합니다.")
    @PostMapping("/add/prescription")
    public ResponseEntity<?> addPrescription(@RequestHeader("access") String token, @RequestBody AddPreReq request) throws IOException {
        User user = medService.getUser(token);
        medService.addPrescription(user, request);

        return ResponseEntity.ok("Prescription and medicines created successfully");
    }

    /**
     * 병용금기 페이지 - 복용중인 약 목록
     */
    @Operation(summary = "복용 중인 약품 조회", description = "복용 중인 약의 리스트를 조회합니다.")
    @GetMapping("/get/medicines")
    public List<GetMedRes> getMedicines(@RequestHeader("access") String token){
        User user = medService.getUser(token);
        Long userId = user.getId();

        return medService.getMedicineList(userId);
    }
}
