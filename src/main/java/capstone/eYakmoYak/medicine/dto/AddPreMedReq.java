package capstone.eYakmoYak.medicine.dto;

import lombok.Getter;

@Getter
public class AddPreMedReq {

    private String name;

    private String dose_time;

    private int meal_time;

    private String imgUrl;
}
