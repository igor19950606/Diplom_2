package User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderService {

    private final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/orders";
    private final String INGREDIENTS_URL = "https://stellarburgers.nomoreparties.site/api/ingredients";
    private final String CONTENT_TYPE = "application/json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response createOrder(List<String> ingredients, String accessToken) {
        try {
            OrderRequest orderRequest = new OrderRequest(ingredients);
            String requestBody = objectMapper.writeValueAsString(orderRequest);
            return given()
                    .contentType(CONTENT_TYPE)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestBody)
                    .when()
                    .post(BASE_URL);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации тела запроса", e);
        }
    }

    public Response createOrderWithoutAuth() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{});

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

    public Response createOrderWithoutAuthWithIngredients(List<String> ingredients) {
        try {
            OrderRequest orderRequest = new OrderRequest(ingredients);
            String requestBody = objectMapper.writeValueAsString(orderRequest);
            return given()
                    .contentType(CONTENT_TYPE)
                    .body(requestBody)
                    .when()
                    .post(BASE_URL);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации", e);
        }
    }
}
