import User.CreateUser;
import User.LoginUser;
import User.OrderService;
import User.ServingUser;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private CreateUser createUser;
    private String accessToken;
    private ServingUser user = new ServingUser();
    private OrderService orderService;
    private static final Faker faker = new Faker();

    @Parameterized.Parameter()
    public String[] ingredients;

    @Parameterized.Parameters()
    public static Iterable<Object[]> data() {
        return OrderTestParameters.data();
    }

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
        orderService = new OrderService();
    }

    @Test
    @Step("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithDynamicIngredients() {
        LoginUser loginUser = LoginUser.fromCreateUser(createUser);
        Response loginResponse = user.loginUser(loginUser);
        String newAccessToken = loginResponse.jsonPath().getString("accessToken");
        if (newAccessToken == null) {
            System.err.println("Access token is null. Response: " + loginResponse.asString());
        } else {
            newAccessToken = newAccessToken.replace("Bearer ", "");
        }
        Response response = orderService.createOrder(Arrays.asList(ingredients), newAccessToken);
        assertThat(response, notNullValue());
        assertThat(response.statusCode(), equalTo(SC_OK));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(true));
        assertThat(response.jsonPath().getInt("order.number"), notNullValue());
    }

    @Test
    @Step("Создание заказа с авторизацией без ингредиентов")
    public void createOrderWithAuthorizationWithoutIngredients() {
        LoginUser loginUser = LoginUser.fromCreateUser(createUser);
        Response loginResponse = user.loginUser(loginUser);
        String newAccessToken = loginResponse.jsonPath().getString("accessToken");
        Response response = orderService.createOrderWithoutAuth();
        assertThat(response, notNullValue());
        assertThat(response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(false));
        assertThat(response.jsonPath().getString("message"), equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа без авторизации и с ингредиентами")
    public void createOrderWithoutAuthorizationWithIngredients() {
        Response response = orderService.createOrderWithoutAuthWithIngredients(Arrays.asList(ingredients));
        assertThat(response, notNullValue());
        assertThat(response.statusCode(), equalTo(SC_OK));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(true));
        assertThat(response.jsonPath().getInt("order.number"), notNullValue());
    }

    @Test
    @Step("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithoutAuthorization() {
        Response response = orderService.createOrderWithoutAuth();
        assertThat(response, notNullValue());
        assertThat(response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(false));
        assertThat(response.jsonPath().getString("message"), equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа с невалидным хешем ингредиента")
    public void createOrderWithInvalidIngredientHash() {
        String[] invalidIngredients = {"hash_1", "hash_2"};
        Response response = orderService.createOrderWithoutAuthWithIngredients(Arrays.asList(invalidIngredients));
        assertThat(response, notNullValue());
        assertThat(response.statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}