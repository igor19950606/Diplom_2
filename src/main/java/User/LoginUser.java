package User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUser {
    private String email;
    private String password;

    public static LoginUser fromCreateUser(CreateUser createUser) {
        return new LoginUser(createUser.getEmail(), createUser.getPassword());
    }
}

