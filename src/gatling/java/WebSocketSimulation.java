import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.WsFrameCheck;

public class WebSocketSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol =
            http.wsBaseUrl("ws://localhost:8080");

    WsFrameCheck webSocketCheck =
            ws.checkTextMessage("WebSocket Check")
                    .check(regex("title").saveAs("title"));

    ChainBuilder webSocket =
            exec(ws("WebSocket Connect").connect("/")
                            .await(20)
                            .on(webSocketCheck))
                    .pause(30)
                    .exec(ws("WebSocket Close").close());

    ScenarioBuilder scenario = scenario("Users").exec(webSocket);

    {
        setUp(scenario.injectOpen(
                rampUsers(30).during(10)
        ))
                .protocols(httpProtocol);
    }

}
