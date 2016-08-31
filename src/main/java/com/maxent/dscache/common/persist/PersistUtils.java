package com.maxent.dscache.common.persist;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by alain on 16/8/31.
 */
public class PersistUtils {
    public static Flusher createFlusher(String name, String dir, String file) {
        Preconditions.checkArgument(new File(dir).exists(), String.format("dir[%s] not exist", dir));

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(name);
        logger.setAdditive(false);

        RollingFileAppender rollingFileAppender = new RollingFileAppender();
        rollingFileAppender.setContext(context);
        rollingFileAppender.setName(name + "_appender");

        rollingFileAppender.setFile(dir + File.separator + file + ".dat");
        rollingFileAppender.setAppend(true);

        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setFileNamePattern(dir + File.separator + file + "_%d{yyyy-MM-dd_HH}.dat");
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg");
        encoder.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();


        logger.addAppender(rollingFileAppender);

        return new Flusher(logger);
    }

    public static void main(String[] args) {
        String name = "test-flusher";
        String dir = "/services/logs";
        String file = "test";
        Flusher flusher = createFlusher(name, dir, file);
        flusher.flush("abc123");
    }
}
