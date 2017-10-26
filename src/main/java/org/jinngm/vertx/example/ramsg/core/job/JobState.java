package org.jinngm.vertx.example.ramsg.core.job;

import io.vertx.codegen.annotations.VertxGen;

/**
 * Created by guming on 2017/10/25.
 */
@VertxGen
public enum JobState {
    INACTIVE,
    ACTIVE,
    COMPLETE,
    FAILED,
    DELAYED
}
