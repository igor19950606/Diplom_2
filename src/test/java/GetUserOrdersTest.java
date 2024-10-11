import User.ServingUser;
import User.CreateUser;
import User.LoginUser;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetUserOrdersTest {

    private ServingUser user;
    private String accessToken;
    private CreateUser createUser;
    private static final Faker faker = new Faker();

    @Before
    public void setUp() {
        user = new ServingUser();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();
        createUser = new CreateUser(email, password, name);
        Response createResponse = user.CreateUser(createUser);
        accessToken = createResponse.jsonPath().getString("accessToken");
        if (accessToken != null) {
            accessToken = accessToken.replace("Bearer ", "");
        } else {
            throw new AssertionError("Не удалось получить токен при создании пользователя.");
        }
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthorization() {
        Response ordersResponse = user.getUserOrders(accessToken);
        assertThat(ordersResponse.statusCode(), equalTo(SC_OK));
        assertThat(ordersResponse.jsonPath().getBoolean("success"), equalTo(true));
        assertThat(ordersResponse.jsonPath().getList("orders"), notNullValue());
    }

    @Test
    @Step("Получение заказов без авторизации")
    public void getUserOrdersWithoutAuthorization() {
        Response ordersResponse = user.getUserOrdersWithoutAuth();
        assertThat(ordersResponse.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat(ordersResponse.jsonPath().getBoolean("success"), equalTo(false));
        assertThat(ordersResponse.jsonPath().getString("message"), equalTo("You should be authorised"));
    }
}
