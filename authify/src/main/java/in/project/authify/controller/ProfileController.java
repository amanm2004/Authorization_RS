package in.project.authify.controller;

import in.project.authify.io.ProfileRequest;
import in.project.authify.io.ProfileResponse;
import in.project.authify.service.ProfileServiceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServiceImp profileServiceImp;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){

      ProfileResponse response =  profileServiceImp.createProfile(request);
      // to do : send  welcome email
      return response;

    }

}
