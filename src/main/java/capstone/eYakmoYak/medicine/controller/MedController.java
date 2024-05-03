package capstone.eYakmoYak.medicine.controller;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.medicine.dto.AddMedReq;
import capstone.eYakmoYak.medicine.dto.AddPreReq;
import capstone.eYakmoYak.medicine.dto.GetMedRes;
import capstone.eYakmoYak.medicine.service.MedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MedController {

    private final MedService medService;

    /**
     * 약품상세정보 조회
     */
    @GetMapping("/get/medicineInfo")
    public ResponseEntity<?> getMedInfo(@RequestParam(value = "itemName", required = false) String itemName){
        String medInfo = medService.getMedInfo(itemName);
        return ResponseEntity.ok(medInfo);
    }

    /**
     * 개별약 등록
     */
    @PostMapping("/add/medicine")
    public ResponseEntity<?> addMedicine(@RequestHeader("access") String token, @RequestBody AddMedReq request){
        User user = medService.getUser(token);
        medService.addMedicine(user, request);

        return ResponseEntity.ok("Medicine created successfully");
    }

    /**
     * 처방약 등록
     */
    @PostMapping("/add/prescription")
    public ResponseEntity<?> addPrescription(@RequestHeader("access") String token, @RequestBody AddPreReq request){
        User user = medService.getUser(token);
        medService.addPrescription(user, request);

        return ResponseEntity.ok("Prescription and medicines created successfully");
    }

    /**
     * 병용금기 페이지 - 복용중인 약 목록
     */
    @GetMapping("/get/medicines")
    public List<GetMedRes> getMedicines(@RequestHeader("access") String token){
        User user = medService.getUser(token);
        Long userId = user.getId();

        return medService.getMedicineList(userId);
    }
}
