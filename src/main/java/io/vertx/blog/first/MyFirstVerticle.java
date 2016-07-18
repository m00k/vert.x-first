package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by m00k on 18.07.16.
 */
public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws Exception {
        vertx
                .createHttpServer()
                .requestHandler(r ->
                        r.response().end("<h1>vert.x first</h1>")
                )
                .listen(8080, result -> {
                    if (result.succeeded()){
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}
