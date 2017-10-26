package org.jinngm.vertx.example.todo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by guming on 2017/10/25.
 */
public class Todo {

    private int id;
    private String title;
    private Boolean completed;
    private Integer order;
    private String url;

    public Todo() {
    }

    public Todo(int id, String title, Boolean completed, Integer order, String url) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.order = order;
        this.url = url;
    }

    public Todo(Todo other) {
        this.id = other.id;
        this.title = other.title;
        this.completed = other.completed;
        this.order = other.order;
        this.url = other.url;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        TodoConverter.toJson(this, json);
        return json;
    }

    public Todo(JsonObject obj) {
        TodoConverter.fromJson(obj, this);
    }

    public Todo(String jsonStr) {
        TodoConverter.fromJson(new JsonObject(jsonStr), this);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Todo todo = (Todo) o;

        if (id != todo.id) return false;
        if (title != null ? !title.equals(todo.title) : todo.title != null) return false;
        if (completed != null ? !completed.equals(todo.completed) : todo.completed != null) return false;
        if (order != null ? !order.equals(todo.order) : todo.order != null) return false;
        return url != null ? url.equals(todo.url) : todo.url == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (completed != null ? completed.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    private static final AtomicInteger acc = new AtomicInteger(0);
    public static int getIncId() {
        return acc.get();
    }

    public static void setIncIdWith(int n) {
        acc.set(n);
    }

    public void setIncId() {
        this.id = acc.incrementAndGet();
    }


}
