package com.maxent.dscache;

import com.maxent.dscache.cache.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by alain on 16/7/19.
 */
public class Main implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApiServer apiServer;

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {


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
            Main restServer = new Main();
            restServer.start();
            while (true) Thread.sleep(1000);
        } catch (InterruptedException e) {
            //ignore
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", e));
        }
    }
}
