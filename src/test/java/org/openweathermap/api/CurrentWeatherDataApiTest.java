package org.openweathermap.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

public class CurrentWeatherDataApiTest {

    public final static String URL = "https://api.openweathermap.org";

    @Test
    @DisplayName("Positive, JSON температуры не пуст")
    public void checkWeatherTempDataNotNullTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL),
                Specifications.responceSpecififcationOK200());
        WeatherTempDataDto tempData = given()
                .param("zip", "83843")
                .when()
                .get("/data/2.5/weather")
                .then()
                .extract().response().body().jsonPath()
                .getObject("main", WeatherTempDataDto.class);

        Assertions.assertNotNull(tempData.getTemp());
        Assertions.assertNotNull(tempData.getFeels_like());
        Assertions.assertNotNull(tempData.getTemp_min());
        Assertions.assertNotNull(tempData.getTemp_max());
        Assertions.assertNotNull(tempData.getPressure());
        Assertions.assertNotNull(tempData.getHumidity());

        Assertions.assertTrue(tempData.getTemp_min() < tempData.getTemp_max(),
                "Temp_min должна быть меньше Temp_max");
    }

    @Test
    @DisplayName("Positive, корректный city")
    public void checkNameCityTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL),
                Specifications.responceSpecififcationOK200());
        Response response = given()
                .param("zip", "83843")
                .when()
                .get("/data/2.5/weather");

        Assertions.assertEquals(response.jsonPath().getString("name"), "Moscow");
    }

    @Test
    @DisplayName("Negative, не указан zip")
    public void checkNameCityNotZipParamTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL));
        given()
                .when()
                .get("/data/2.5/weather")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Positive, ответ в формате html")
    public void checkResponseHTMLformatTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL),
                Specifications.responceSpecififcationOK200());
        given()
                .param("zip", "83843")
                .param("mode", "html")
                .when()
                .get("/data/2.5/weather")
                .then()
                .contentType("text/html");
    }

    @Test
    @DisplayName("Positive, параметр \"q\" в запросе с названием города")
    public  void checkSearchCityParamTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL),
                Specifications.responceSpecififcationOK200());
        Response response = given()
                .param("q", "Moscow")
                .when()
                .get("/data/2.5/weather");

        Assertions.assertEquals(response.jsonPath().getString("name"), "Moscow");
        Assertions.assertEquals(response.jsonPath().getInt("id"), 524901);
    }

    @Test
    @DisplayName("Negative, невалидный zip")
    public void notValidZipSendParamTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL));
        Response response = given()
                .param("zip", "236001")
                .when()
                .get("/data/2.5/weather");

        Assertions.assertEquals(response.jsonPath().getString("cod"), "404");
        Assertions.assertEquals(response.jsonPath().getString("message"), "city not found");
    }

    @Test
    @DisplayName("Positive, валидный id города")
    public void searchIdCityParamTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL),
                Specifications.responceSpecififcationOK200());
        Response response = given()
                .param("id", 524901)
                .when()
                .get("/data/2.5/weather");

        Assertions.assertEquals(response.jsonPath().getString("name"), "Moscow");
        Assertions.assertEquals(response.jsonPath().getInt("id"), 524901);
    }

    @Test
    @DisplayName("Negative, невалидный id города")
    public void searchNotValidIdCityParamTest() {
        Specifications.setUpSpecification(Specifications.requestSpecification(URL));
        Response response = given()
                .param("id", 524901111)
                .when()
                .get("/data/2.5/weather");

        Assertions.assertEquals(response.jsonPath().getString("cod"), "404");
        Assertions.assertEquals(response.jsonPath().getString("message"), "city not found");
    }
}
