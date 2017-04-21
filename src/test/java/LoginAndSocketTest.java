import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

/**
 * Created by Sergei_Shatilov on 4/4/2017.
 */

public class LoginAndSocketTest {

    CountDownLatch latch;

    @Test
    public void checkLogin()
    {
        baseURI  = "https://auth.iqoption.com";

        Response response = given()
                .header("Cookie[lang]", "ru_RU")
                .header("Accept-Language", "ru")
                .param("email", "sergey039@gmail.com")
                .param("password", "sergey039")
                .when()
                .post(baseURI + "/api/v1.0/login")
                .then()
                .statusCode(200)
                .and().extract().response();


        final String ssid = response.cookie("ssid");
        boolean domain = response.header("Set-Cookie").contains("Domain=.iqoption.com");
        boolean max_age = response.header("Set-Cookie").contains("Max-Age=2592000");

        System.out.println(ssid);
        System.out.println(domain);
        System.out.println(max_age);
        System.out.println();

//        ServerEndpointConfig.Builder.create(WebsocketClientEndpoint.class, "/echo/578/io83ikw4/websocket").build();
//        ClientEndpointConfig.Builder.create().build();
//    }
//
//    @Test
//    public void openSocket(){

        Random random = new Random();
        for(int i=0;i<1;i++)
        {
            System.out.println(" Generated number is  : "+random.nextInt(1000));
        }

        int length = 8;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers).toLowerCase();

        System.out.println(generatedString);

        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("wss://iqoption.com:443/echo/"+random.nextInt(1000)+"/"+generatedString+"/websocket"));

            latch = new CountDownLatch(1);
                    // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    try {
                        System.out.println(message);
                        if (message.length() < 5) {
                            return;
                        }
                        Message msg = MessageUtil.convert(message);
                        if ("timeSync".equals(msg.getName())) {
                            Message ssidMessage = new Message();
                            ssidMessage.setName("ssid");
                            ssidMessage.setRequestId(UUID.randomUUID().toString());
                            ssidMessage.setMsg(ssid);
                            String sendMsg = MessageUtil.toTransfer(ssidMessage);
                            System.out.println(sendMsg);
                            clientEndPoint.sendMessage(sendMsg);
                        }
                        if ("profile".equals(msg.getName())) {
                            System.out.println(msg.getMsg());
                            Map profile = (Map) msg.getMsg();
                            Integer userId = (Integer) profile.get("user_id");
                            System.out.println("userId == " + userId);
                            latch.countDown();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            boolean messageRecieved = latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue("Message", messageRecieved);

        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
