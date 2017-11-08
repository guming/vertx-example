package org.jinngm.vertx.example;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by guming on 2017/11/2.
 */
public class Eventbus {
    public static void main(String[] args) {
        EventBus eb = Vertx.vertx().eventBus();
        eb.consumer("news.uk.sport", message -> {
            System.out.println("I have received a message: " + message.body());
        }).completionHandler(res -> {
            if (res.succeeded()) {
                System.out.println("The handler registration has reached all nodes");
            } else {
                System.out.println("Registration failed!");
            }
        });
        eb.consumer("news.uk.sport", message -> {
            System.out.println("you have received a message: " + message.body());
        });
        eb.publish("news.uk.sport", "Yay! Someone kicked a ball");
        eb.send("news.uk.sport", "Yay! Someone kicked a ball2");
    }
}
