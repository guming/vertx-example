package org.jinngm.vertx.example.ramsg.core.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.jinngm.vertx.example.ramsg.core.job.JobState;

/**
 * Created by guming on 2017/10/30.
 */
public final class RedisHelper {
    private static final String VERTX_KUE_REDIS_PREFIX = "vertx_ramsg";
    private RedisHelper() {
    }
    public static RedisClient client(Vertx vertx, JsonObject config) {
        return RedisClient.create(vertx, options(config));
    }

    public static RedisOptions options(JsonObject config) {
        return new RedisOptions()
                .setHost(config.getString("redis.host", "127.0.0.1"))
                .setPort(config.getInteger("redis.port", 6379));
    }

    public static String getKey(String key) {
        return VERTX_KUE_REDIS_PREFIX + ":" + key;
    }

    public static String getStateKey(JobState state) {
        return VERTX_KUE_REDIS_PREFIX + ":jobs:" + state.name();
    }


}
