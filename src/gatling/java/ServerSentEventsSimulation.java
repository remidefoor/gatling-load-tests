import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ServerSentEventsSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://34.138.203.98:3000");

    SseMessageCheck serverSentEventsCheck =
            sse.checkMessage("Server-sent Events Check")
                    .check(regex("title").saveAs("title"));

    ChainBuilder serverSentEvents =
            exec(sse("Server-sent Events Connect").connect("/event-stream")
                            .await(10)
                            .on(serverSentEventsCheck))
                    .pause(300)
                    .exec(sse("Server-sent Events Close").close());

    ScenarioBuilder openScenario = scenario("Open").exec(serverSentEvents);
    ScenarioBuilder closedScenario = scenario("Closed").exec(serverSentEvents);

    public PopulationBuilder runOpenScenario() {
        return openScenario.injectOpen(
                rampUsers(100).during(60),
                rampUsers(150).during(240),
                constantUsersPerSec(1).during(300).randomized()

        );
    }

    public PopulationBuilder runClosedScenario() {
        return closedScenario.injectClosed(
                rampConcurrentUsers(0).to(100).during(60),
                rampConcurrentUsers(100).to(250).during(240),
                constantConcurrentUsers(250).during(300)
        );
    }

    {
        setUp(
                runClosedScenario()
        )
                .protocols(httpProtocol);
    }

}
