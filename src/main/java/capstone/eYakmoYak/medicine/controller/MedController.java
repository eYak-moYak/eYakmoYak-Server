package capstone.eYakmoYak.medicine.controller;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.medicine.dto.*;
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
    public ResponseEntity<?> addMedicine(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AddMedReq request) throws IOException {

        String token = authorizationHeader.substring(7);

        User user = medService.getUser(token);
        medService.addMedicine(user, request);

        return ResponseEntity.ok("Medicine created successfully");
    }

    /**
     * 처방약 등록
     */
    @Operation(summary = "처방약 등록", description = "처방 받은 약의 리스트를 등록합니다.")
    @PostMapping("/add/prescription")
    public ResponseEntity<?> addPrescription(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AddPreReq request) throws IOException {

        String token = authorizationHeader.substring(7);

        User user = medService.getUser(token);
        medService.addPrescription(user, request);

        return ResponseEntity.ok("Prescription and medicines created successfully");
    }

    /**
     * 병용금기 페이지 - 복용중인 약 목록
     */
    @Operation(summary = "복용 중인 약품 조회", description = "복용 중인 약의 리스트를 조회합니다.")
    @GetMapping("/get/medicines")
    public List<GetMedRes> getMedicines(@RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.substring(7);

        User user = medService.getUser(token);
        Long userId = user.getId();

        return medService.getMedicineList(userId);
    }

    /**
     * 병용금기 페이지 - 병용 금기 조회
     */
    @Operation(summary = "병용 금기 조회", description = "병용 금기 리스트를 조회합니다.")
    @GetMapping("/get/cont/medicines")
    @ResponseBody
    public List<GetContRes> getContraindication(@RequestParam("medicines") List<String> medicines, @RequestParam("name") String name) throws IOException {

        return medService.getContList(medicines, name);
    }

    /**
     * 유저의 처방전 & 개별약
     */
    @Operation(summary = "유저의 처방전, 개별약 조회", description = "유저의 처방전과 개별약 리스트를 조회합니다.")
    @GetMapping("/get/premedList")
    public GetInfoList getUserPreAndMed(@RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.substring(7);

        User user = medService.getUser(token);
        Long userId = user.getId();

        return medService.getUserPreAndMed(userId);
    }

    /**
     * 하나의 처방전 조회
     */
    @Operation(summary = "하나의 처방전 조회", description = "처방전 정보 및 약 목록을 조회합니다.")
    @GetMapping("/get/prescription/{preId}")
    public GetPrescription getPrescription(@PathVariable("preId") Long id){
        return medService.getPrescription(id);
    }

}
