package org.jinngm.vertx.example.ramsg.core.queue;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.jinngm.vertx.example.ramsg.core.job.Job;
import org.jinngm.vertx.example.ramsg.core.job.JobService;
import org.jinngm.vertx.example.ramsg.core.util.Constans;
import org.jinngm.vertx.example.ramsg.core.util.RedisHelper;

/**
 * Created by guming on 2017/11/7.
 */
public class Kue {

    private final JsonObject config;
    private final Vertx vertx;
    private final JobService jobService;
    private final RedisClient client;

    public Kue(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
        this.jobService = JobService.createProxy(vertx, Constans.EB_JOB_SERVICE_ADDRESS);
        this.client = RedisHelper.client(vertx, config);
        Job.setVertx(vertx, RedisHelper.client(vertx, config)); // init static vertx instance inner job
    }

}
