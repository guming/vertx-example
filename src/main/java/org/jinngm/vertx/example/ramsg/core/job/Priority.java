package org.jinngm.vertx.example.ramsg.core.job;

import io.vertx.codegen.annotations.VertxGen;

/**
 * Created by guming on 2017/10/25.
 */
@VertxGen
public enum Priority {
    LOW(10),
    NORMAL(0),
    MEDIUM(-5),
    HIGH(-10),
    CRITICAL(-15);
    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
