package in.project.authify.service;

import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import org.springframework.stereotype.Service;


public interface ProfileService {

   ProfileResponse createProfile(ProfileRequest request);

   ProfileResponse getProfile(String email);

   void  sendResetOtp(String email);

   void resetPassword(String email,String otp,String newPassword);

   void sendOtp(String email);

   void verifyOtp(String email,String otp);


}
