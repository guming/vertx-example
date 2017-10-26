package org.jinngm.vertx.example.todo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.List;
import java.util.Optional;

/**
 * Created by guming on 2017/10/25.
 */
public class TodoRedisStorage implements TodoService {

    private final Vertx vertx;
    private final RedisClient redisClient;
    private final RedisOptions config;

    public TodoRedisStorage(Vertx vertx, RedisOptions config) {
        this.vertx = vertx;
        this.config = config;
        this.redisClient = RedisClient.create(vertx);
    }

    public TodoRedisStorage(RedisOptions config) {
        this(Vertx.vertx(), config);
    }

    @Override
    public Future<Boolean> init() {
        return save(new Todo(Math.abs(new java.util.Random().nextInt()),
                "Something to do...", false, 1, "todo/ex"));
    }

    @Override
    public Future<Boolean> save(Todo todo) {
        Future<Boolean> result = Future.future();
        final String encoded = Json.encodePrettily(todo);
        redisClient.hset("VERT_TODO", String.valueOf(todo.getId()), encoded, res -> {
            if(res.succeeded()){
                result.complete(true);
            }else{
                result.fail(res.cause());
            }
        });
        return result;
    }

    @Override
    public Future<List<Todo>> getAll() {
        return null;
    }

    @Override
    public Future<Optional<Todo>> get(String todoId) {
        Future<Optional<Todo>> result = Future.future();
        redisClient.hget(Constants.REDIS_TODO_KEY, todoId, res -> {
            if (res.succeeded()) {
                result.complete(Optional.ofNullable(
                        res.result() == null ? null : new Todo(res.result())));
            } else
                result.fail(res.cause());
        });
        return result;
    }

    @Override
    public Future<Boolean> delete(String todoId) {
        return null;
    }

    @Override
    public Future<Boolean> deleteAll() {
        return null;
    }

    @Override
    public Future<Boolean> update(String todoId, Todo newTodo) {
        return null;
    }
}
