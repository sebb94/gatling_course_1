package computerdatabase.ScriptFundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.List;

public class MyFirstTest extends Simulation {

  private static ChainBuilder getAllVideoGames = 
        repeat(3).on(
          (
            exec
            (
              http
              ("Get all games")
              .get("/videogame")
              .check(status().is(200))
            )
          )
        );

  private static ChainBuilder getSpecificVideoGame = 
  repeat(5, "i").on(
    (
      exec
      (
        http
        (
        "Get game with #{i}")
        .get("/videogame/#{i}")
        .check(status().is(200)
        )
      )
    )
  );

  private static ChainBuilder getSpecificVideoGameWithAnID(String a) {
   return exec
    (
      http
      (
      "Get game with id " + a)
      .get("/videogame/" + a)
      .check(status().is(200)
      )
    );
  }

  private static ChainBuilder authenticate =
  exec(http("Authenticate")
          .post("/authenticate")
          .body(StringBody("{\n" +
                  "  \"password\": \"admin\",\n" +
                  "  \"username\": \"admin\"\n" +
                  "}"))
          .check(jmesPath("token").saveAs("jwtToken")));

private static ChainBuilder createNewGame =
  exec(http("Create new game")
          .post("/videogame")
          .header("Authorization", "Bearer #{jwtToken}")
          .body(StringBody(
                  "{\n" +
                          "  \"category\": \"Platform\",\n" +
                          "  \"name\": \"Mario 128\",\n" +
                          "  \"rating\": \"Mature\",\n" +
                          "  \"releaseDate\": \"2022-05-04\",\n" +
                          "  \"reviewScore\": 85\n" +
                          "}"
          )));
   

  // 1. http configuration

  private HttpProtocolBuilder httpProtocol = http
  .baseUrl("https://videogamedb.uk/api")
  .acceptHeader("application/json")
  .contentTypeHeader("application/json");
  
  // 2. scenario definition

  private ScenarioBuilder scn =  scenario("MyFirstTest")
  .exec(authenticate)
  .pause(2)
  .exec(createNewGame)
  .exec(getSpecificVideoGameWithAnID("3"))
  .exec(getAllVideoGames)
  .pause(2)
  .exec(getSpecificVideoGame)
  .pause(2)
  .repeat(5)
  .on(exec(getAllVideoGames))
  ;

  private ScenarioBuilder scn2 =  scenario("MySecondTest")
          .exec
          (
            http("Get specific game - id 1")
            .get("/videogame/1")
            .check(status().in(200,201,202))
          )
          .pause(1,10) // randomowo od 1 do 10 sekund
          .exec
          (
            http
            ("Get all games - 1st call")
            .get("/videogame")
            .check(status().is(200))
            .check(jmesPath("[? id == `1`].name").ofList().is(List.of("Resident Evil 4")))
            //.check(jsonPath("$[?(@.id==1)].name").is("Resident Evil 4"))   
          )
          .pause(5) // 5 sekund
          .exec
          (
            http
            ("Get all games - 2nd call")
            .get("/videogame")
            .check(status().not(400), status().not(401))
            .check(jmesPath("[3].id").saveAs("gameId"))
          )
          .pause(Duration.ofMillis(4000)) // 4 sekundy
          .exec 
          (
            session -> {
              System.out.println(session);
              System.out.println(session.getString("gameId") + " is game id!");
              return session;
            }
          )
          .exec
          (
            http
            ("Get game with id #{gameId}")
            .get("/videogame/#{gameId}")
            .check(jmesPath("name").is("Super Mario 64"))
          )
          .exec 
          (
            session -> {
              System.out.println(session.getString("responseBody") + " asd");
              return session;
            }
          )
          .pause(Duration.ofMillis(4000)) // 4 sekundy
          ; 

  // 3. load simulation

  {
    setUp(
      scn.injectOpen(atOnceUsers(1))
    ).protocols(httpProtocol);

  }
  
}
