import User.CreateUser;
import User.LoginUser;
import User.ServingUser;
import User.UserData;
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

    @After
    public void userCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Before
    public void setUp() {
        createUser = new CreateUser("test-users@yandex.ru", "1234", "TestUser");
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
        UserData updatedData = new UserData("UpdatedTestUser", "updated-test-users@yandex.ru");
        response = user.updateUser("Bearer " + newAccessToken, updatedData);
        int statusCode = response.statusCode();
        assertThat(response, notNullValue());
        assertThat(statusCode, equalTo(SC_OK));
        String updatedName = response.jsonPath().getString("user.name");
        String updatedEmail = response.jsonPath().getString("user.email");
        assertThat(updatedName, equalTo("UpdatedTestUser"));
        assertThat(updatedEmail, equalTo("updated-test-users@yandex.ru"));
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void updateUserDataWithoutAuthorization() {
        UserData updatedData = new UserData("TestUser", "test-users@yandex.ru");
        response = user.updateUserWithoutAuth(updatedData);
        int statusCode = response.statusCode();
        assertThat(response, notNullValue());
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
        String errorMessage = response.jsonPath().getString("message");
        assertThat(errorMessage, equalTo("You should be authorised"));
    }
}