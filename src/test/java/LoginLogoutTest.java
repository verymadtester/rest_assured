import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

/**
 * Created by Sergei_Shatilov on 4/5/2017.
 */
public class LoginLogoutTest {

    @Test
    public void login_logout() {

        baseURI  = "https://auth.iqoption.com";

        Response response = given()
                .header("Cookie[lang]", "ru_RU")
                .header("Accept-Language", "ru")
                .param("email", "sergey039@gmail.com")
                .param("password", "sergey039")
                .when().post(baseURI + "/api/v1.0/login").then()
                .statusCode(200)
                .and().extract().response();

        String ssid = response.cookie("ssid");
        boolean domain = response.header("Set-Cookie").contains("Domain=.iqoption.com");
        boolean max_age = response.header("Set-Cookie").contains("Max-Age=2592000");

        System.out.println("ssid: " + ssid);
        System.out.println("domain: " + domain);
        System.out.println("max_age: " + max_age);

        Response response1 = given()
                .cookie("ssid", ssid)
                .cookie("lang", "ru_RU")
                .when().post(baseURI + "/api/v1.0/logout").then()
                .statusCode(200)
                .and().extract().response();

        boolean new_max_age = response1.header("Set-Cookie").contains("Max-Age=0");

        System.out.println("new_max_age: " + new_max_age);

    }

}
