package org.jinngm.vertx.example.ramsg.core.job;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;

/**
 * Created by guming on 2017/10/25.
 */
@DataObject(generateConverter = true)
public class Job {
    private static Logger logger = LoggerFactory.getLogger(Job.class);
    private static Vertx vertx;
    private static RedisClient redis;
    private static EventBus eventBus;

    public static void setVertx(Vertx v, RedisClient redisClient) {
        vertx = v;
        redis = redisClient;
        eventBus = vertx.eventBus();
    }

    //props
    private long id=-1;
    private String uuid;
    private String type;
    private JsonObject data;
    private JobState state=JobState.INACTIVE;
    private long delay=0;
    private int max_attempts=1;
    private boolean removeOnComplete = false;
    private int ttl = 0;
    private JsonObject backoff;
    private int attempts = 0;
    private int progress = 0;
    private JsonObject result;
    private Priority priority = Priority.NORMAL;
    //metrics
    private long created_at;
    private long promote_at;
    private long updated_at;
    private long failed_at;
    private long started_at;
    private long duration;

    private void _checkStatic() {
        if (vertx == null) {
            logger.warn("static Vertx instance in Job class is not initialized!");
        }
    }





    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getMax_attempts() {
        return max_attempts;
    }

    public void setMax_attempts(int max_attempts) {
        this.max_attempts = max_attempts;
    }

    public boolean isRemoveOnComplete() {
        return removeOnComplete;
    }

    public void setRemoveOnComplete(boolean removeOnComplete) {
        this.removeOnComplete = removeOnComplete;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public JsonObject getBackoff() {
        return backoff;
    }

    public void setBackoff(JsonObject backoff) {
        this.backoff = backoff;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getPromote_at() {
        return promote_at;
    }

    public void setPromote_at(long promote_at) {
        this.promote_at = promote_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public long getFailed_at() {
        return failed_at;
    }

    public void setFailed_at(long failed_at) {
        this.failed_at = failed_at;
    }

    public long getStarted_at() {
        return started_at;
    }

    public void setStarted_at(long started_at) {
        this.started_at = started_at;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
