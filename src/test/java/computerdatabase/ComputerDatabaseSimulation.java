package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ComputerDatabaseSimulation extends Simulation {

    FeederBuilder<String> feeder = tsv("search.tsv").random();

    ChainBuilder create = feed(feeder).exec(
        http("creating user").post("/v1/dummy").body(StringBody(StringBody("#{payload}")))
                .header("content-type", "application/json")
    );

    // repeat is a loop resolved at RUNTIME
    ChainBuilder browse = exec(
                    http("busca")
                            .get("/v1/dummy/01J4PN7BXDGR9VZ3B7AYC7Y6WK")
            );

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:9000")
            .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
            );

    ScenarioBuilder users = scenario("Users").exec(browse);

    {
        setUp(
            users.injectOpen(constantUsersPerSec(700).during(240))
        ).protocols(httpProtocol);
    }
}
