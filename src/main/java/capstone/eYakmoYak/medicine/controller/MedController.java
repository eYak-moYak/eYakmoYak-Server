package capstone.eYakmoYak.medicine.controller;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.medicine.dto.AddMedReq;
import capstone.eYakmoYak.medicine.service.MedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MedController {

    private final MedService medService;

    /**
     * 약품상세정보 조회
     */
    @GetMapping("/search/medicine")
    public ResponseEntity<?> searchMedInfo(@RequestParam(value = "itemName", required = false) String itemName){
        String medInfo = medService.getMedInfo(itemName);
        return ResponseEntity.ok(medInfo);
    }

    /**
     * 개별약등록
     */
    @PostMapping("/add/medicine")
    public ResponseEntity<?> addMedicine(@RequestHeader("access") String token, @RequestBody AddMedReq request){
        User user = medService.getUser(token);
        medService.addMedicine(user, request);

        return ResponseEntity.ok("Medicine created successfully");
    }
}
