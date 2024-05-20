package capstone.eYakmoYak.medicine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetInfoList {
    private List<GetMedList> medicines;
    private List<GetPreList> prescriptions;
}
