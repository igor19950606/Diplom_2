package User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUser {
    private String email;
    private String password;
    private String name;
}