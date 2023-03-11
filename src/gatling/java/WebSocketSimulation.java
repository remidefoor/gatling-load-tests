import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.WsFrameCheck;

public class WebSocketSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol =
            http.wsBaseUrl("ws://34.148.212.17934.148.212.179:8080");

    WsFrameCheck webSocketCheck =
            ws.checkTextMessage("WebSocket Check")
                    .check(regex("title").saveAs("title"));

    ChainBuilder webSocket =
            exec(ws("WebSocket Connect").connect("/")
                            .await(5)
                            .on(webSocketCheck))
                    .pause(300)
                    .exec(ws("WebSocket Close").close());

    ScenarioBuilder openScenario = scenario("Open").exec(webSocket);
    ScenarioBuilder closedScenario = scenario("Closed").exec(webSocket);

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
