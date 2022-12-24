package computerdatabase.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;


import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class loadTest extends Simulation {

  private static ChainBuilder getAllVideoGames = 
      exec(http("Get all games")
        .get("/posts")
        .check(status().is(200)
        ));

    private static ChainBuilder getSpecificVideoGame = 
      exec(http("Get game with id 2}")
        .get("/posts/1")
        .check(status().is(200)
      ));

      // 1. http configuration

  private HttpProtocolBuilder httpProtocol = http
  .baseUrl("http://localhost:3000/")
  .acceptHeader("application/json")
  .contentTypeHeader("application/json");
  
  // 2. scenario definition

  private ScenarioBuilder scn =  scenario("MyFirstTest")
  .exec(getAllVideoGames)
  .pause(2)
  .exec(getSpecificVideoGame);

      {
        setUp(
          scn.injectOpen(
            nothingFor(4), // 1
            atOnceUsers(10), // 2
            rampUsers(10).during(5), // 3
            constantUsersPerSec(20).during(15), // 4
            constantUsersPerSec(20).during(15).randomized(), // 5
            rampUsersPerSec(10).to(20).during(10), // 6
            rampUsersPerSec(10).to(20).during(10).randomized(), // 7
            stressPeakUsers(1000).during(20) // 8
          )
        ).protocols(httpProtocol);
      }

  }
