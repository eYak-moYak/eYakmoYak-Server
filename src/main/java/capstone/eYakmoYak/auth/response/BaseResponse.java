package capstone.eYakmoYak.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResponse {
    private boolean isSuccess;
    private String code;
    private String message;

    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = false;
        this.code = status.name();
        this.message = status.getMessage();
    }
}
