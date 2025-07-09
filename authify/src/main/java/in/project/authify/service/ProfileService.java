package in.project.authify.service;

import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import org.springframework.stereotype.Service;


public interface ProfileService {

   ProfileResponse createProfile(ProfileRequest request);
}
