package capstone.eYakmoYak.medicine.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class AddPreReq {
    private String pre_name;

    private String hospital;

    private String pharmacy;

    private LocalDate pre_date;

    private LocalDate start_date;

    private LocalDate end_date;

    private List<AddPreMedReq> medicines;

}
