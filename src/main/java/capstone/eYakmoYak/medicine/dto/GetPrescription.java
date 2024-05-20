package capstone.eYakmoYak.medicine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class GetPrescription {

    private String pre_name;

    private LocalDate pre_date;

    private String hospital;

    private String pharmacy;

    private List<GetMedList> medicines;

}
