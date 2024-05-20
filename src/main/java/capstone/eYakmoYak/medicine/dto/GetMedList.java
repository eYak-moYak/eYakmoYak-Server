package capstone.eYakmoYak.medicine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GetMedList {
    private String name;

    private LocalDate start_date;

    private LocalDate end_date;

    private String dose_time;

    private int meal_time;

}
