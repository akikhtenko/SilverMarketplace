package com.silverbars.marketplace.orderboard;

import com.google.common.collect.ImmutableList;
import com.silverbars.marketplace.orderboard.order.RegisterOrder;
import com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static com.silverbars.marketplace.orderboard.order.RegisterOrder.Builder.aRegisterOrderCommand;
import static com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo.Builder.anOrderSummary;
import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderBoardSystemTest {
    private static final String BASE_PATH = "/marketplace/board/orders";
    private static final String BUFFETT = "wbuffett";
    private static final String SOROS = "gsoros";
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final BigDecimal TWENTY = new BigDecimal(20);

    @LocalServerPort
    private int serverPortNumber;

    @Test
    public void should_allow_valid_order_registration() {
        RegisterOrder validOrder =
                aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser(BUFFETT).build();

        given().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .body(validOrder).with().contentType(JSON).log().body()
                .when().post()
                .then().assertThat().statusCode(is(OK.getStatusCode()));
    }

    @Test
    public void should_deny_invalid_order_registration() {
        RegisterOrder invalidOrder = aRegisterOrderCommand().ofType(BUY).withQuantity(TEN).forUser(BUFFETT).build();

        given().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .body(invalidOrder).with().contentType(JSON).log().body()
                .when().post()
                .then().log().body().and().assertThat()
                        .statusCode(is(BAD_REQUEST.getStatusCode())).and()
                        .body("message", is("Order registration failed"))
                        .body("details", is("Price level must be provided"));
    }

    @Test
    public void should_report_registered_orders_summary() {
        registerNewOrder(aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser(BUFFETT).build());
        registerNewOrder(aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser(BUFFETT).build());
        registerNewOrder(aRegisterOrderCommand().ofType(BUY).withPriceLevel(TWO).withQuantity(TEN).forUser(SOROS).build());
        registerNewOrder(aRegisterOrderCommand().ofType(BUY).withPriceLevel(TEN).withQuantity(TWENTY).forUser(SOROS).build());
        registerNewOrder(aRegisterOrderCommand().ofType(SELL).withPriceLevel(TWO).withQuantity(TEN).forUser(BUFFETT).build());
        registerNewOrder(aRegisterOrderCommand().ofType(SELL).withPriceLevel(ONE).withQuantity(TEN).forUser(SOROS).build());
        cancelOrder(
                registerNewOrder(aRegisterOrderCommand().ofType(SELL).withPriceLevel(ONE).withQuantity(TWENTY).forUser(BUFFETT).build()));

        OrderSummaryInfo orderSummary = with().port(serverPortNumber).and().basePath(BASE_PATH)
                .get("summary")
                .then().log().body().and().extract().response().as(OrderSummaryInfo.class);

        assertThat(orderSummary, is(anOrderSummary()
                .withSellSideSummaries(
                        ImmutableList.of(
                                aSummary().forPriceLevel(ONE).withQuantity(TEN).build(),
                                aSummary().forPriceLevel(TWO).withQuantity(TEN).build()
                        ))
                .withBuySideSummaries(
                        ImmutableList.of(
                                aSummary().forPriceLevel(TEN).withQuantity(TWENTY).build(),
                                aSummary().forPriceLevel(TWO).withQuantity(TEN).build(),
                                aSummary().forPriceLevel(ONE).withQuantity(TWENTY).build()
                        )).build()));
    }

    @Test
    public void should_advise_when_cancelling_non_registered_order() {
        given().port(serverPortNumber).and().basePath(BASE_PATH).and()
                        .request().with().pathParam("orderId", "unregistered")
                .when().delete("{orderId}")
                .then().assertThat()
                        .statusCode(is(NOT_FOUND.getStatusCode())).and()
                        .body("message", is("Non existing order unregistered could not be cancelled"));
    }

    @Test
    public void should_cancel_previously_registered_order() {
        String orderId = registerNewOrder(aRegisterOrderCommand()
                .ofType(BUY).forUser(BUFFETT).withQuantity(TEN).withPriceLevel(ONE).build());

        given().port(serverPortNumber).and().basePath(BASE_PATH).and().request().with().pathParam("orderId", orderId)
                .when().delete("{orderId}")
                .then().assertThat().statusCode(is(OK.getStatusCode()));
    }

    private String registerNewOrder(RegisterOrder anOrder) {
        return with().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .body(anOrder).with().contentType(JSON).log().body().and().post()
                .then().extract().response().asString();
    }

    private void cancelOrder(String orderId) {
        with().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .request().with().pathParam("orderId", orderId).delete("{orderId}");
    }
}
