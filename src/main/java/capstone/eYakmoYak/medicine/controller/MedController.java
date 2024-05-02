package capstone.eYakmoYak.medicine.controller;

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
}
