package org.jinngm.vertx.example.todo;

import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

/**
 * Created by guming on 2017/10/25.
 */
public interface TodoService {
    Future<Boolean> init();
    Future<Boolean> save(Todo todo);
    Future<List<Todo>> getAll();
    public Future<Optional<Todo>> get(String todoId);
    Future<Boolean> delete(String todoId);
    Future<Boolean> deleteAll();
    Future<Boolean> update(String todoId,Todo newTodo);
}
