package computerdatabase.challenge;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class challenge extends Simulation {

  private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "10"));
  private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "5"));
  private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "20"));

  @Override
  public void before() {
      System.out.printf("Running test with %d users%n", USER_COUNT);
      System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
      System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
  }

  @Override
  public void after() {
      System.out.printf("TEST COMPLETE!");
      System.out.printf("Running test with #{lastID}");
  }

  private static Iterator<Map<String, Object>> customFeeder =
  Stream.generate((Supplier<Map<String, Object>>) () -> {

      String title = RandomStringUtils.randomAlphanumeric(10) + "-title";
      String author = "by: " + RandomStringUtils.randomAlphanumeric(8) ;
      HashMap<String, Object> hmap = new HashMap<String, Object>();
      hmap.put("title", title);
      hmap.put("author", author);
      return hmap;
}
  ).iterator();



  private static ChainBuilder getAllPosts = 
      exec(http("Get all post")
        .get("/posts")
        .check(status().is(200)
        ));

    private static ChainBuilder getSpecificPost = 
      exec(http("Get post with id 2}")
        .get("/posts/1")
        .check(status().is(200)
      ));

      private static ChainBuilder createPost = 
      feed(customFeeder)
      .exec(http("Create new post")
      .post("/posts")
      .body(ElFileBody("bodies/newPostTemplate.json")).asJson()
      .check(status().in(200,201))
      );

      String url = "ad";

      private static ChainBuilder getLastCreatedPost = 

    
      exec(http("Get last posts ")
      .get("/posts")
      .check(jmesPath("[?title == '#{title}'].id").saveAs("lastID"))
     
      );

      private static ChainBuilder sessionReturn =

      exec(session -> {
        Session newSession = session.set("lastID", "#{lastID}");
        String a = session.get("lastID");
        a = a.replaceAll("\\[(.*?)\\]", "$1");
        newSession = session.set("lastID", a);
        return newSession;
      });



      private static ChainBuilder deletePost = 
      exec(http("Delete new post")
      .delete("/posts/#{lastID}")
      );

      // 1. http configuration

  private HttpProtocolBuilder httpProtocol = http
  .baseUrl("http://localhost:3000/")
  .acceptHeader("application/json")
  .contentTypeHeader("application/json");
  
  // 2. scenario definition

  private ScenarioBuilder scn =  scenario("MyFirstTest")
      .exec(createPost)
      .pause(5)
      .exec(getLastCreatedPost)
      .exec(sessionReturn)
      .pause(5)
      .exec(deletePost)
      ;
     
      // .exec(getAllPosts)
      // .pause(5)
      // .exec(createPost)
      // .pause(5)
      // .exec(getAllPosts);

    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        atOnceUsers(USER_COUNT),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                ).protocols(httpProtocol)
        );
        //.maxDuration(TEST_DURATION);
    }

  }
