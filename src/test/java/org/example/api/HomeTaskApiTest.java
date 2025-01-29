package org.example.api;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.store.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class HomeTaskApiTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeTaskApiTest.class);
    private static int orderId;

    @BeforeClass
    public void prepare() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());

    }

    @Test
    public void checkObjectSave() {
        Order order = new Order();
        int id = new Random().nextInt(10) + 1;
        int petId = new Random().nextInt(500000) + 1;
        int quantity = new Random().nextInt(100) + 1;
        OffsetDateTime shipDate = OffsetDateTime.now(ZoneOffset.UTC);
        Order.Status status = Order.Status.values()[new Random().nextInt(Order.Status.values().length)];

        order.setId(id);
        order.setPetId(petId);
        order.setQuantity(quantity);
        order.setShipDate(shipDate);
        order.setStatus(status);
        order.setComplete(true);

        LOGGER.info("Создаем заказ: {}", order);

        orderId = id;

        given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);

        LOGGER.info("Заказ {} успешно создан", id);

        LOGGER.info("Получаем заказ {} и сравниваем значения полей из POST и GET запросов", id);

        Order actual =
                given()
                        .pathParam("orderId", id)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String actualDateString = actual.getShipDate().format(formatter);
        String expectedDateString = order.getShipDate().format(formatter);

        Assert.assertEquals(actualDateString, expectedDateString, "shipDate не совпадает");
        Assert.assertEquals(actual.getPetId(),order.getPetId(),"petId не совпадает");
        Assert.assertEquals(actual.getQuantity(), order.getQuantity(),"quantity не совпадает");
        Assert.assertEquals(actual.getStatus(), order.getStatus(),"status не совпадает");
        Assert.assertEquals(actual.isComplete(), order.isComplete(),"complete не совпадает");

    }

    @Test
    public void testDelete() {

        LOGGER.info("Удаляем заказ {}", orderId);

        given()
                .pathParam("orderId",orderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);

        LOGGER.info("Отправляем GET запрос для проверки удаления заказа {}", orderId);

        //Проверяем, что заказ удален
        int statusCode = given()
                .pathParam("orderId", orderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(statusCode,404,"Заказ не удален" );

        LOGGER.info("Заказ {} успешно удален", orderId);
    }

    @Test
    public void testInventoryResponse() {

        LOGGER.info("Отправляем GET запрос на /store/inventory");

        Response response = given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .extract().response();

        Map<String, Object> inventory = response.as(new TypeRef<Map<String, Object>>() {});

        Assert.assertTrue(inventory.containsKey("available"), "Inventory не содержит статус available");
    }
}
