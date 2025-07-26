package in.project.authify.service;

import in.project.authify.entity.UserEntity;
import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import in.project.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
         throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists!");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found"+email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found:"+email));

        //generate 6 digit random number
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //calculate expiry time(current time + 15 minutes in milliseconds)
        long expiryTime =System.currentTimeMillis()+ (15 * 60 *1000);

        //update profile/user
        userEntity.setResetOtp(otp);
        userEntity.setResetOtpExpireAt(expiryTime);

        //save into database
        userRepository.save(userEntity);

        try {
              emailService.sendResetOtpEmail(userEntity.getEmail(),otp);
        }catch (Exception ex){
            throw new RuntimeException("unable to send email");
        }


    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+email));
        if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid otp");
        }
        if (existingUser.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("Otp Expired");
        }
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + email));

        if (existingUser.getIsAccountVerified() !=null && existingUser.getIsAccountVerified()){
            return;
        }

        // generate 6 digit OTP
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        // calculate expiry time (current time + 24 hours in milliseconds )
        long expiryTime =System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        // update user entity
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        // save into database
        userRepository.save(existingUser);

        try {
            emailService.sendOtpEmail(existingUser.getEmail(),otp);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }


    }
    @Override
    public void verifyOtp(String email, String otp) {
       UserEntity existingUser = userRepository.findByEmail(email)
               .orElseThrow(()-> new UsernameNotFoundException("User not found "+email));
       if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
              throw new RuntimeException("Invalid OTP");
       }
       if (existingUser.getVerifyOtpExpireAt()< System.currentTimeMillis()){
           throw new RuntimeException("OTP is expired");
       }

       existingUser.setIsAccountVerified(true);
       existingUser.setVerifyOtp(null);
       existingUser.setVerifyOtpExpireAt(0L);

       userRepository.save(existingUser);
    }



    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
   return  ProfileResponse.builder()
           .userId(newProfile.getUserId())
           .name(newProfile.getName())
           .email(newProfile.getEmail())
           .isAccountVerified(newProfile.getIsAccountVerified())
           .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
      return   UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0l)
                .verifyOtp(null)
                .verifyOtpExpireAt(0l)
                .resetOtp(null)
                .build();

    }
}
