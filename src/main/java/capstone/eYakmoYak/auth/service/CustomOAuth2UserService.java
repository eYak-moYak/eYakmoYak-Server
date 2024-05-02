package capstone.eYakmoYak.auth.service;

import capstone.eYakmoYak.auth.domain.User;
import capstone.eYakmoYak.auth.dto.*;
import capstone.eYakmoYak.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }

        else if(registrationId.equals("naver")){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }

        else {

            return null;
        }
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User existData = userRepository.findByUsername(username);

        if (existData == null) {

            User user = new User();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());
            user.setName(oAuth2Response.getName());

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName());

            return new CustomOAuth2User(userDTO);
        }
    }
}
