package capstone.eYakmoYak.medicine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GetPreList {

    private String pre_name;

    private LocalDate pre_date;

    private String hospital;

    private String pharmacy;

    private int countMedicine;
}
