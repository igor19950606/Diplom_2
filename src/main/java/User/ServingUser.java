package User;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ServingUser {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String USER_ENDPOINT = "/api/auth/register";
    private static final String DELETE_ENDPOINT = BASE_URL + "/api/auth/user";
    private static final String LOGIN_ENDPOINT = BASE_URL + "/api/auth/login";
    private static final String UPDATE_ENDPOINT = BASE_URL + "/api/auth/user";
    private static final String ORDERS_ENDPOINT = BASE_URL + "/api/orders";

    public Response CreateUser(CreateUser createuser) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(createuser)
                .post(USER_ENDPOINT);
    }

    public Response loginUser(LoginUser loginUser) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(loginUser)
                .post(LOGIN_ENDPOINT);
    }

    public Response deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_ENDPOINT);
    }

    public Response updateUser(String accessToken, UserData userData) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .body(userData)
                .patch(UPDATE_ENDPOINT);
    }

    public Response updateUserWithoutAuth(UserData userData) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(userData)
                .patch(UPDATE_ENDPOINT);
    }

    public Response getUserOrders(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .get(ORDERS_ENDPOINT);
    }

    public Response getUserOrdersWithoutAuth() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .get(ORDERS_ENDPOINT);
    }
}