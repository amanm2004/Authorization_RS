package in.project.authify.util;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    public String generateToken (UserDetails userDetails){
        return "ajaj";
    }
}
