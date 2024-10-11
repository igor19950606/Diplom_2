import User.CreateUser;
import User.ServingUser;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {

    private ServingUser user = new ServingUser();
    private CreateUser createuser;
    private String accessToken;
    private static final Faker faker = new Faker();

    @After
    public void userCleaning() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Before
    public void setUp() {
        accessToken = null;
    }

    @Test
    @Step("Создание пользователя с заполнением всех полей")
    public void userCreate() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();
        createuser = new CreateUser(email, password, name);
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Создание пользователя, который уже зарегистрирован")
    public void userDouble() {
        String email = "test-double@yandex.ru";
        String password = "12324";
        String name = "Double";
        createuser = new CreateUser(email, password, name);
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
        String password = faker.internet().password();
        String name = faker.name().firstName();
        createuser = new CreateUser("", password, name);
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
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        createuser = new CreateUser(email, "", name);
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
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        createuser = new CreateUser(email, password, "");
        Response response = user.CreateUser(createuser);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}


