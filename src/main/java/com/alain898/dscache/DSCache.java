package com.alain898.dscache;

import com.alain898.dscache.cache.*;
import com.alain898.dscache.cache.exceptions.CacheHostExistException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Created by alain on 16/7/19.
 */
public class DSCache implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(DSCache.class);

    private ApiServer apiServer;

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {


    }

    private void postApiServerStart(Config config) throws Exception {
        String host = config.getString("server.ip");
        int port = config.getInt("server.port");
        try {
            CacheClusterService.getInstance().addHosts(Arrays.asList(new Host(host, port)), true);
        } catch (CacheHostExistException e) {
            logger.info(String.format("host[%s:%d] already exist", host, port));
        }
    }

    @Override
    public void start() throws Exception {
        try {
            logger.info("starting...");

            Config config = ConfigFactory.load();

            // initialize cache cluster info in zookeeper if not initialized.
            CacheClusterInitializer cacheClusterInitializer = new CacheClusterInitializer(config);
            cacheClusterInitializer.initClusterIfNot();

            // config CacheClusterViewer factory before it's used
            CacheClusterViewerFactory.configure(config);

            // config CacheClusterService
            CacheClusterService.configure(config);

            // config SubCacheService
            SubCacheService.configure(config);

            // start api server
            apiServer = new ApiServer(config);
            apiServer.start();

            postApiServerStart(config);

            logger.info("start server successfully.");
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", ExceptionUtils.getStackTrace(e)));
            throw e;
        }

    }

    @Override
    public void stop() throws Exception {
        logger.info("stopping...");
        try {
            apiServer.stop();
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", ExceptionUtils.getStackTrace(e)));
            throw e;
        }
        logger.info("stopped successfully.");
    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) throws Exception {
        try {
            DSCache dsCache = new DSCache();
            dsCache.start();
            while (true) Thread.sleep(1000);
        } catch (InterruptedException e) {
            //ignore
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", e));
        }
    }
}
