package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by m00k on 18.07.16.
 */
public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws Exception {
        int port = config().getInteger("http.port", 8080);
        Router router = initRouter(vertx);

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

        return router;
    }
}
