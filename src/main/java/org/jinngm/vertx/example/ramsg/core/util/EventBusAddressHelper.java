package org.jinngm.vertx.example.ramsg.core.util;

import org.jinngm.vertx.example.ramsg.core.job.Job;

/**
 * Created by guming on 2017/11/9.
 */
public class EventBusAddressHelper {
    public static String getCertainJobAddress(String handlerType, Job job) {
        return "vertx.kue.handler.job." + handlerType + "." + job.getUuid() + "." + job.getType();
    }

}
