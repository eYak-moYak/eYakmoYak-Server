package capstone.eYakmoYak.auth.dto;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return (String) ((Map) attribute.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {

        return (String) ((Map) attribute.get("properties")).get("nickname");
    }
}
