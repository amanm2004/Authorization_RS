package in.project.authify.controller;


import in.project.authify.io.AuthRequest;
import in.project.authify.io.AuthResponse;
import in.project.authify.service.AppUserDetailService;
import in.project.authify.util.JwtUtil;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.sql.internal.DiscriminatedAssociationPathInterpretation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AppUserDetailService appUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
        try {
            authenticate(authRequest.getEmail(),authRequest.getPassword());
            final UserDetails userDetails =appUserDetailService.loadUserByUsername(authRequest.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("strict")
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new AuthResponse(authRequest.getEmail(), jwtToken));

        }catch (BadCredentialsException ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Email or Password incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch (DisabledException ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        catch (Exception ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email != null);
    }
}
