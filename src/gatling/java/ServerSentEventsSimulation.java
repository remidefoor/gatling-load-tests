import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ServerSentEventsSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:3000");

    SseMessageCheck serverSentEventsCheck =
            sse.checkMessage("Server-sent Events Check")
                    .check(regex("title").saveAs("title"));

    ChainBuilder serverSentEvents =
            exec(sse("Server-sent Events Connect").connect("/event-stream")
                            .await(5)
                            .on(serverSentEventsCheck))
                    .pause(30)
                    .exec(sse("Server-sent Events Close").close());

    ScenarioBuilder scenario = scenario("Users").exec(serverSentEvents);

    {
        setUp(scenario.injectOpen(rampUsers(30).during(10)))
                .protocols(httpProtocol);
    }

}
