package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
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

        // enable reading of request body for all routes under /api/whiskies
        router
            .route("/api/whiskies*")
            .handler(BodyHandler.create());

        router
            .post("/api/whiskies")
            .handler(this::add);

        router
            .delete("/api/whiskies/:id")
            .handler(this::delete);

        return router;
    }

    private void delete(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext
                .response()
                .setStatusCode(400)
                .end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            whiskies.remove(idAsInteger);
        }
        routingContext
            .response()
            .setStatusCode(204) // no content
            .end();
    }

    private void add(RoutingContext routingContext) {
        final Whisky whisky = Json.decodeValue(
            routingContext.getBodyAsString(),
            Whisky.class
        );
        whiskies.put(whisky.getId(), whisky);

        // status 201 created
        // and return new entity in body
        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(whisky));
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
