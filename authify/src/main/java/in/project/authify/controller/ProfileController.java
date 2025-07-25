package in.project.authify.controller;

import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import in.project.authify.service.EmailService;
import in.project.authify.service.ProfileServiceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServiceImp profileServiceImp;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){

      ProfileResponse response =  profileServiceImp.createProfile(request);
      emailService.sendWelcomeMail(response.getEmail(),response.getName());
      return response;

    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
         return  profileServiceImp.getProfile(email);

    }



}
