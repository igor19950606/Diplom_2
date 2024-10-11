import User.CreateUser;
import User.LoginUser;
import User.ServingUser;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginTest {
    private ServingUser user;
    private CreateUser createuser;
    private String accessToken;
    private static final Faker faker = new Faker();

    @After
    public void UserCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Before
    public void before() {
        user = new ServingUser();
        String email = faker.internet().emailAddress();
        createuser = new CreateUser(email, "1234", "TestUser");
        Response createResponse = user.CreateUser(createuser);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Логин под существующим пользователем")
    public void userLoginSuccess() {
        LoginUser loginUser = LoginUser.fromCreateUser(createuser);
        Response loginResponse = user.loginUser(loginUser);
        assertResponseStatus(loginResponse, HttpStatus.SC_OK);
    }

    @Test
    @Step("Логин с неверным логином и паролем")
    public void userLoginWithInvalid() {
        LoginUser invalidLoginUser = new LoginUser("test-usersNull@yandex.ru", "TestUser");
        Response loginResponse = user.loginUser(invalidLoginUser);
        assertResponseStatus(loginResponse, HttpStatus.SC_UNAUTHORIZED);
        assertErrorMessage(loginResponse, "email or password are incorrect");
    }

    @Test
    @Step("Логин с отсутствующим полем password")
    public void userLoginWithMissingFieldPassword() {
        LoginUser missingPasswordUser = new LoginUser(createuser.getEmail(), "");
        Response loginResponse = user.loginUser(missingPasswordUser);
        assertResponseStatus(loginResponse, HttpStatus.SC_UNAUTHORIZED);
        assertErrorMessage(loginResponse, "email or password are incorrect");
    }

    @Test
    @Step("Логин с отсутствующим полем email")
    public void userLoginWithMissingFieldEmail() {
        LoginUser missingEmailUser = new LoginUser("", "1234");
        Response loginResponse = user.loginUser(missingEmailUser);
        assertResponseStatus(loginResponse, HttpStatus.SC_UNAUTHORIZED);
        assertErrorMessage(loginResponse, "email or password are incorrect");
    }
    private void assertResponseStatus(Response response, int expectedStatus) {
        assertThat(response.statusCode(), is(expectedStatus));
    }
    private void assertErrorMessage(Response response, String expectedMessage) {
        String errorMessage = response.jsonPath().getString("message");
        assertThat(errorMessage, is(expectedMessage));
    }
}
