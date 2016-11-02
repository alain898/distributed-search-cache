package com.alain898.dscache.common.persist;

import ch.qos.logback.classic.Logger;

/**
 * Created by alain on 16/8/31.
 */
public class Flusher {
    private Logger logger;

    public Flusher(Logger logger) {
        this.logger = logger;
    }

    public void flush(String content){
        logger.info(content);
    }
}
