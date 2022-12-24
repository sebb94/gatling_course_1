package computerdatabase;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://svc-dev.stepstone.tools")
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("apikey", "05a6b1dd515d46e5ac6d637fe99a95ce"),
    Map.entry("sec-ch-ua", "Not?A_Brand\";v=\"8\", \"Chromium\";v=\"108\", \"Google Chrome\";v=\"108"),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "Windows"),
    Map.entry("sec-fetch-dest", "empty"),
    Map.entry("sec-fetch-mode", "cors"),
    Map.entry("sec-fetch-site", "same-origin")
  );


  private ScenarioBuilder scn = scenario("RecordedSimulation")
    .exec(
      http("/ld/listing-display-api-de/v1/listings/3897386/header/?lang=de")
        .get("/ld/listing-display-api-de/v1/listings/3897386/header/?lang=de")
        .headers(headers_0)
    )
    .pause(15)
    .exec(
      http("/ld/listing-display-api-de/v1/listings/3897386/predictedSalary/")
        .get("/ld/listing-display-api-de/v1/listings/3897386/predictedSalary/")
        .headers(headers_0)
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(20))).protocols(httpProtocol);
  }
}
