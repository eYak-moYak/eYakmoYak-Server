package capstone.eYakmoYak.medicine.dto;


import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AddMedReq {

    private String name;

    private LocalDate start_date;

    private LocalDate end_date;

    private String dose_time;

    private int meal_time;

    private String imgUrl;
}
