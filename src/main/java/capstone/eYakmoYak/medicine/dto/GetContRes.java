package capstone.eYakmoYak.medicine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetContRes {

    private String name;

    private String reason;
}
