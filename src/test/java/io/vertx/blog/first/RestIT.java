package io.vertx.blog.first;

import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by m00k on 19.07.16.
 */
public class RestIT {
    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.getInteger("http.port", 8080);
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void shouldGetById() {
        // Get the list of bottles, ensure it's a success and extract the first id.
        final int id = get("/api/whiskies").then()
            .assertThat()
            .statusCode(200)
            .extract()
            .jsonPath().getInt("find { whisky -> whisky.name=='Bowmore 15 Years Laimrig' }.id");
        // Now get the individual resource and check the content
        get("/api/whiskies/" + id).then()
            .assertThat()
            .statusCode(200)
            .body("name", equalTo("Bowmore 15 Years Laimrig"))
            .body("origin", equalTo("Scotland, Islay"))
            .body("id", equalTo(id));
    }

    @Test
    public void shouldAddAndDelete() {
        final String name = "Fancy Name";
        final String origin = "fancy place";
        final String whiskyJson =
            "{\"name\":\"" + name + "\", \"origin\":\"" + origin + "\"}";

        // add
        Whisky whisky = given()
            .body(whiskyJson)
            .request()
            .post("/api/whiskies")
            .thenReturn()
            .as(Whisky.class); // how to test for status code?

        assertThat(whisky.getName()).isEqualTo(name);
        assertThat(whisky.getOrigin()).isEqualTo(origin);
        assertThat(whisky.getId()).isNotZero();

        // get
        get("/api/whiskies/" + whisky.getId()).then()
            .assertThat()
            .statusCode(200)
            .body("name", equalTo(name))
            .body("origin", equalTo(origin))
            .body("id", equalTo(whisky.getId()));

        // delete
        delete("/api/whiskies/" + whisky.getId()).then()
            .assertThat()
            .statusCode(204);

        // confirm deletion
        get("/api/whiskies/" + whisky.getId()).then()
            .assertThat()
            .statusCode(404);
    }
}
