package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by m00k on 18.07.16.
 */
public class MyFirstVerticle extends AbstractVerticle {

    private Map<Integer, Whisky> whiskies = new LinkedHashMap<>();

    @Override
    public void start(Future<Void> fut) throws Exception {
        int port = config().getInteger("http.port", 8080);
        Router router = initRouter(vertx);

        whiskies = initWhiskies();

        vertx
            .createHttpServer()
            .requestHandler(router::accept)
            .listen(
                port,
                result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                }
            );
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(whiskies.values()));
    }

    private Router initRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        router
            .route("/")
            .handler(routingContext -> {
                HttpServerResponse response = routingContext.response();
                response
                    .putHeader("content-type", "text/html")
                    .end("<h1>vert.x first</h1>")
                ;
            });

        router
            .route("/assets/*")
            .handler(StaticHandler.create("assets"));

        router
            .get("/api/whiskies")
            .handler(this::getAll);

        return router;
    }

    private Map<Integer, Whisky> initWhiskies() {
        Map<Integer, Whisky> whiskies = new LinkedHashMap<>();

        Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
        whiskies.put(bowmore.getId(), bowmore);

        Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
        whiskies.put(talisker.getId(), talisker);

        return whiskies;
    }
}
