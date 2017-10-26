package org.jinngm.vertx.example.todo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisOptions;
import org.jinngm.vertx.example.Runner;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by guming on 2017/10/25.
 */
public class TodoVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoVerticle.class);
    private static final String HOST = "0.0.0.0";
    private static final int PORT = 8082;
    private TodoService service;
    private void init() {
        RedisOptions config = new RedisOptions()
                .setHost(config().getString("redis.host", "127.0.0.1"))
                .setPort(config().getInteger("redis.port", 6379));
        service = new TodoRedisStorage(vertx, config);
        service.init().setHandler(res -> {
            if(res.failed()){
                LOGGER.error("Persistence service is not running!");
                res.cause().printStackTrace();
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        init();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders).allowedMethods(allowMethods));

        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.post(Constants.API_CREATE).handler(this::handleSaveTodo);

        server.requestHandler(router::accept)
                .listen(config().getInteger("http.port", PORT),
                        config().getString("http.address", HOST), result -> {
                            if (result.succeeded())
                                startFuture.complete();
                            else
                                startFuture.fail(result.cause());
                        });
    }

    private void handleGetTodo(RoutingContext context){
       String todoId = context.request().getParam("todoId");
       LOGGER.info("todoId:"+todoId);
       service.get(todoId).setHandler(res->{
           if(res.succeeded()) {
               HttpServerResponse response = context.response();
               if (res.result().isPresent()) {
                   final String encoded = Json.encodePrettily(res.result().get());
                   response.putHeader("content-type", "application/json").end(encoded);
               }else{
                   response.putHeader("content-type", "application/json").end("{}");
               }

           }else{
               serviceUnavailable(context);
           }
       });
    }

    private void handleSaveTodo(RoutingContext context){
        Todo todo = wrapObject(new Todo(context.getBodyAsString()), context);
        final String encoded = Json.encodePrettily(todo);
        service.save(todo).setHandler(res -> {
            if(res.succeeded()){
                context.response().setStatusCode(201)
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }else{
                serviceUnavailable(context);
            }
        });
    }

    private void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
    }
    private Todo wrapObject(Todo todo, RoutingContext context) {
        int id = todo.getId();
        if (id > Todo.getIncId()) {
            Todo.setIncIdWith(id);
        } else if (id == 0)
            todo.setIncId();
        todo.setUrl(context.request().absoluteURI() + "/" + todo.getId());
        return todo;
    }
    public static void main(String[] args) {
        Runner.runExample(TodoVerticle.class);
    }
}
