package org.jinngm.vertx.example.ramsg.core.job;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import org.jinngm.vertx.example.ramsg.core.queue.Kue;
import org.jinngm.vertx.example.ramsg.core.util.EventBusAddressHelper;
import org.jinngm.vertx.example.ramsg.core.util.RedisHelper;

import java.util.Objects;
import java.util.UUID;

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
    private String zid;
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


    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
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

    public Job setProgress(int progress) {
        this.progress = progress;
        return this;
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

    public Job() {
        this.uuid = UUID.randomUUID().toString();
        _checkStatic();
    }

    public Job(String type, JsonObject data) {
        this.type = type;
        this.data = data;
        this.uuid = UUID.randomUUID().toString();
        _checkStatic();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JobConvert.toJson(this, json);
        return json;
    }

    /**
     * persistent method
     */
    public Future<Job> active(){
        return this.state(JobState.ACTIVE);
    }

    public Future<Job> inactive(){
        return this.state(JobState.ACTIVE);
    }

    public Future<Job> delay(){
        return this.state(JobState.DELAYED);
    }

    public Future<Job> complete(){
        return this.setProgress(100).set("progress","100")
                .compose(Job::updateNow)
                .compose(r->r.state(JobState.COMPLETE));
    }

    public Future<Job> fail(){
        this.failed_at = System.currentTimeMillis();
        return this.updateNow()
                .compose(r -> r.set("failed_at",String.valueOf(this.failed_at)))
                .compose(j -> j.state(JobState.FAILED));
    }

    /*
        job entity update
     */
    public Future<Job> set(String key, String value) {
        Future<Job> future = Future.future();
        redis.hset(RedisHelper.getKey("job:" + this.id), key, value, r -> {
            if (r.succeeded())
                future.complete(this);
            else
                future.fail(r.cause());
        });
        return future;
    }
    /*
            get attr of job
         */
    @Fluent
    public Future<String> get(String key) {
        Future<String> future = Future.future();
        redis.hget(RedisHelper.getKey("job:" + this.id), key, future.completer());
        return future;
    }

    /*
        job persistent method
     */
    public Future<Job> state(JobState newState){
        Future<Job> future = Future.future();
        RedisClient redisClient = RedisClient.create(vertx);
        JobState oldState = this.state;
        redisClient.transaction().multi(h -> {
            if (h.succeeded()) {
                if (oldState != null && !oldState.equals(newState)) {
                    redisClient.transaction().zrem(RedisHelper.getKey("jobs:" + this.type + ":" + oldState.name()), this.zid, _failure())
                            .zrem(RedisHelper.getStateKey(oldState), this.zid, _failure());
                }
                redisClient.transaction().hset(RedisHelper.getKey("job:" + this.id), "state", newState.name(), _failure())
                        .zadd(RedisHelper.getKey("jobs:" + this.type + ":" + newState.name()), this.priority.getValue(), this.zid, _failure())
                        .zadd(RedisHelper.getStateKey(newState), this.priority.getValue(), this.zid, _failure());
                switch (newState) { // dispatch different state
                    case ACTIVE:
                        redisClient.transaction().zadd(RedisHelper.getKey("jobs:" + newState.name()),
                                this.priority.getValue() < 0 ? this.priority.getValue() : -this.priority.getValue(),
                                this.zid, _failure());
                        break;
                    case DELAYED:
                        redisClient.transaction().zadd(RedisHelper.getKey("jobs:" + newState.name()),
                                this.promote_at, this.zid, _failure());
                        break;
                    case INACTIVE:
                        redisClient.transaction().lpush(RedisHelper.getKey(this.type + ":jobs"), "1", _failure());
                        break;
                    default:
                }

                this.state = newState;

                redisClient.transaction().exec(e -> {
                    if (e.succeeded()) {
                        future.complete(this);
                    } else {
                        future.fail(e.cause());
                    }
                });


            } else {
                future.fail(h.cause());
            }
            });
        return future.compose(Job::updateNow);
    }

    /*
           job persistent method
        */
    public Future<Job> save(){
        Objects.requireNonNull(this.type,"this job type must not be null");
        if(this.id>0){

        }
        Future<Job> future = Future.future();
        redis.incr(RedisHelper.getKey("ids"),r->{
            if(r.succeeded()){
                this.id = r.result();
                this.zid = RedisHelper.createFIFO(id);
                String key = RedisHelper.getKey("job:" + this.id);
                if(this.delay>0){
                    this.state = JobState.DELAYED;
                }
                this.created_at = System.currentTimeMillis();
                this.promote_at = this.created_at + this.delay;
                redis.sadd(RedisHelper.getKey("job:types"), this.type, _failure());
                redis.hmset(key,this.toJson(),_complete(future,this));
            }else {
                future.fail(r.cause());
            }
        });
        return future.compose(Job::update);
    }
    /*
           job persistent method
        */
    public Future<Job> update(){
        Future future = Future.future();
        this.updated_at = System.currentTimeMillis();
        redis.transaction()
                .multi(_failure())
//                .hset(RedisHelper.getKey("job:" + this.id), "updated_at", String.valueOf(this.updated_at), _failure())
                .zadd(RedisHelper.getKey("jobs"), priority.getValue(), this.zid, _failure())
                .exec(_complete(future, this));
        return future.compose(r-> this.state(this.state));
    }
    /*
           job persistent method
        */
    public Future<Job> updateNow() {
        this.updated_at = System.currentTimeMillis();
        return this.set("updated_at", String.valueOf(updated_at));
    }
    /*
           job persistent method
        */
    public Future<Void> remove(){
        Future future = Future.future();
        redis.transaction().multi(_failure())
                .zrem(RedisHelper.getKey("jobs:" + this.state.name()), this.zid, _failure())
                .zrem(RedisHelper.getKey("jobs:" + this.type + ":" + this.state.name()), this.zid, _failure())
                .zrem(RedisHelper.getKey("jobs"), this.zid, _failure())
                .del(RedisHelper.getKey("job:" + this.id + ":log"), _failure())
                .del(RedisHelper.getKey("job:" + this.id), _failure())
                .exec(r->{
                    if(r.succeeded()){
                        this.emit("remove", new JsonObject().put("id", this.id));
                        future.complete();
                    }else{
                        future.fail(r.cause());
                    }
                });
        return future;
    }

    public Future<Job> log(String msg) {
        Future<Job> future = Future.future();
        redis.rpush(RedisHelper.getKey("job:" + this.id + ":log"), msg, _complete(future, this));
        return future.compose(Job::updateNow);
    }





    private Job emit(String jobEvent,Object msg){
        logger.info("jobEvent:"+jobEvent+",msg:"+msg);
        eventBus.send(EventBusAddressHelper.getCertainJobAddress(jobEvent,this), msg);
        return this;
    }

    private <T> Job on(String jobEvent,Handler<Message<T>> handler){
        eventBus.consumer(EventBusAddressHelper.getCertainJobAddress(jobEvent,this),handler);
        return this;
    }

    private static <T> Handler<AsyncResult<T>>_failure(){
        return r->{
            if(r.failed()){
                r.cause().printStackTrace();
            }
        };
    }

    private static <T,R> Handler<AsyncResult<T>>_complete(Future<R> future, R result){
        return r->{
            if(r.failed()){
                future.fail(r.cause());
            }else {
                future.complete(result);
            }
        };
    }
}
