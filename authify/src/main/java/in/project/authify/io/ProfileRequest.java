package in.project.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Name should not be empty")
    private String name;
    @Email(message = "Email is not valid")
    @NotNull(message = "Email should not be empty")
    private String email;
    @Size(min = 6,message = "Password must be atleast 6 characters")
    private String password;
}
