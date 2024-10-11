import User.CreateUser;
import User.LoginUser;
import User.ServingUser;
import User.UserData;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChangingUserDataTest {
    private CreateUser createUser;
    private String accessToken;
    private ServingUser user = new ServingUser();
    private Response response;
    private static final Faker faker = new Faker();

    @After
    public void userCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Before
    public void setUp() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();
        createUser = new CreateUser(email, password, name);
        Response createResponse = user.CreateUser(createUser);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Изменение данных пользователя с авторизацией")
    public void updateUserDataWithAuthorization() {
        LoginUser loginUser = LoginUser.fromCreateUser(createUser);
        Response loginResponse = user.loginUser(loginUser);
        String newAccessToken = loginResponse.jsonPath().getString("accessToken");
        newAccessToken = newAccessToken.replace("Bearer ", "");
        assertThat(loginResponse, notNullValue());
        assertThat(loginResponse.statusCode(), equalTo(SC_OK));
        String newName = faker.name().firstName();
        String newEmail = faker.internet().emailAddress();
        UserData updatedData = new UserData(newName, newEmail);
        response = user.updateUser("Bearer " + newAccessToken, updatedData);
        int statusCode = response.statusCode();
        assertThat(response, notNullValue());
        assertThat(statusCode, equalTo(SC_OK));
        String updatedName = response.jsonPath().getString("user.name");
        String updatedEmail = response.jsonPath().getString("user.email");
        assertThat(updatedName, equalTo(newName));
        assertThat(updatedEmail, equalTo(newEmail));
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void updateUserDataWithoutAuthorization() {
        UserData updatedData = new UserData(faker.name().firstName(), faker.internet().emailAddress());
        response = user.updateUserWithoutAuth(updatedData);
        int statusCode = response.statusCode();
        assertThat(response, notNullValue());
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
        String errorMessage = response.jsonPath().getString("message");
        assertThat(errorMessage, equalTo("You should be authorised"));
    }
}