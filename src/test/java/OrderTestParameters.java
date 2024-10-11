import User.OrderService;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

public class OrderTestParameters {

    public static Iterable<Object[]> data() {
        OrderService orderService = new OrderService();
        Response ingredientsResponse = orderService.getIngredients();
        assertThat(ingredientsResponse.statusCode(), equalTo(SC_OK));
        List<String> availableIngredients = ingredientsResponse.jsonPath()
                .getList("data._id", String.class);
        return Arrays.asList(new Object[][]{
                {new String[]{availableIngredients.get(0), availableIngredients.get(1)}},
                {new String[]{availableIngredients.get(1), availableIngredients.get(2)}},
                {new String[]{availableIngredients.get(2), availableIngredients.get(3)}},
                {new String[]{availableIngredients.get(0), availableIngredients.get(1), availableIngredients.get(2)}}
        });
    }
}

