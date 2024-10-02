package User;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrderService {

    private final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/orders";
    private final String INGREDIENTS_URL = "https://stellarburgers.nomoreparties.site/api/ingredients";
    private final String CONTENT_TYPE = "application/json"; // Тип контента

    public Response createOrder(String[] ingredients, String accessToken) {
        String requestBody = String.format("{\"ingredients\": [\"%s\"]}", String.join("\", \"", ingredients));
        return given()
                .contentType(CONTENT_TYPE)
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .post(BASE_URL);
    }

    public Response createOrderWithoutAuth() {
        String requestBody = "{\"ingredients\": []}";
        return given()
                .contentType(CONTENT_TYPE)
                .body(requestBody)
                .when()
                .post(BASE_URL);
    }

    public Response getIngredients() {
        return given()
                .contentType(CONTENT_TYPE)
                .when()
                .get(INGREDIENTS_URL);
    }

    public Response createOrderWithoutAuthWithIngredients(String[] ingredients) {
        String requestBody = String.format("{\"ingredients\": [\"%s\"]}", String.join("\", \"", ingredients));
        return given()
                .contentType(CONTENT_TYPE)
                .body(requestBody)
                .when()
                .post(BASE_URL);
    }
}