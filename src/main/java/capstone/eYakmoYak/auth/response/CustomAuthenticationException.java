package capstone.eYakmoYak.auth.response;


import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    private final AuthenticationErrorCode errorCode;

    public CustomAuthenticationException(AuthenticationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthenticationErrorCode getErrorCode() {
        return errorCode;
    }
}
