package in.project.authify.service;

import in.project.authify.entity.UserEntity;
import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import in.project.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService{


    private final UserRepository userRepository;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
         throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists!");
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
   return  ProfileResponse.builder()
           .userId(newProfile.getUserId())
           .name(newProfile.getName())
           .email(newProfile.getEmail())
           .isAccountVerified(newProfile.isAccountVerified())
           .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
      return   UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(request.getPassword())
                .isAccountVerified(false)
                .resetOtpExpireAt(0l)
                .verifyOtp(null)
                .verifyOtpExpireAt(0l)
                .resetOtp(null)
                .build();

    }
}
