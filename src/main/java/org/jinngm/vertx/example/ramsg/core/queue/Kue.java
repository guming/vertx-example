package org.jinngm.vertx.example.ramsg.core.queue;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.jinngm.vertx.example.ramsg.core.job.JobService;

/**
 * Created by guming on 2017/11/7.
 */
public class Kue {

    private final JsonObject config;
    private final Vertx vertx;
    private final JobService jobService;
    private final RedisClient client;


}
