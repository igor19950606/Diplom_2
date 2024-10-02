import User.CreateUser;
import User.LoginUser;
import User.ServingUser;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginTest {
    private ServingUser user = new ServingUser();
    private CreateUser createuser;
    private String accessToken;

    @After
    public void UserCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Before
    public void before() {
        createuser = new CreateUser("test-users@yandex.ru", "1234", "TestUser");
        Response createResponse = user.CreateUser(createuser);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Логин под существующим пользователем")
    public void userLoginSuccess() {
        LoginUser loginUser = LoginUser.fromCreateUser(createuser);
        Response loginResponse = user.loginUser(loginUser);
        loginResponse.then().assertThat().statusCode(HttpStatus.SC_OK);
    }
    @Test
    @Step("Логин с неверным логином и паролем")
    public void userLoginWithInvalid() {
        LoginUser invalidLoginUser = new LoginUser("test-usersNull@yandex.ru", "TestUser");
        Response loginResponse = user.loginUser(invalidLoginUser);
        loginResponse.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat(errorMessage, is("email or password are incorrect"));
    }

    @Test
    @Step("Логин с отсутствующим полем password")
    public void userLoginWithMissingFieldPassword() {
        LoginUser missingPasswordUser = new LoginUser("test-users@yandex.ru", "");
        Response loginResponse = user.loginUser(missingPasswordUser);
        loginResponse.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat(errorMessage, is("email or password are incorrect"));
    }
    @Test
    @Step("Логин с отсутствующим полем email")
    public void userLoginWithMissingFieldEmail() {
        LoginUser missingPasswordUser = new LoginUser("", "1234");
        Response loginResponse = user.loginUser(missingPasswordUser);
        loginResponse.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat(errorMessage, is("email or password are incorrect"));
    }
}
