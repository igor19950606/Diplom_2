import User.CreateUser;
import User.ServingUser;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {

    private ServingUser user = new ServingUser();
    private CreateUser createuser;
    private String accessToken;

    @After
    public void UserCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Создание пользователя с заполнением всех полей")
    public void userCreate() {
        createuser = new CreateUser("test-users@yandex.ru", "1234", "TestUser");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo("test-users@yandex.ru"))
                .body("user.name", equalTo("TestUser"));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Создание пользователя, который уже зарегистрирован")
    public void userDouble() {
        createuser = new CreateUser("test-double@yandex.ru", "12324", "Double");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Step("Создание пользователя c не заполненным полем Email")
    public void userNullEmail() {
        createuser = new CreateUser("", "12324", "NullEmail");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Step("Создание пользователя c не заполненным полем Password")
    public void userNullPassword() {
        createuser = new CreateUser("test-nullpassword@yandex.ru", "", "NullPassword");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Step("Создание пользователя c не заполненным полем Name")
    public void userNullName() {
        createuser = new CreateUser("test-nullname@yandex.ru", "1234", "");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}



