package com.maxent.dscache;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.maxent.dscache.api.rest.RestResource;
import com.maxent.dscache.cache.CacheClusterService;
import com.maxent.dscache.cache.CacheClusterViewerFactory;
import com.maxent.dscache.cache.SubCacheService;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Properties;


/**
 * Created by alain on 16/7/19.
 */
public class Main implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_REST_SERVER_IP = "127.0.0.1";
    private static final int DEFAULT_REST_SERVER_PORT = 5232;
    private Server server;

    private String ip;
    private int port;

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {


    }

    @Override
    public void start() throws Exception {
        try {
            logger.info("starting...");

            Properties props = new Properties();
            props.load(Main.class.getClassLoader().getResourceAsStream("conf.properties"));
            ip = props.getProperty("ip", DEFAULT_REST_SERVER_IP);
            port = Integer.parseInt(props.getProperty("port", String.valueOf(DEFAULT_REST_SERVER_PORT)));

            ResourceConfig config = new RestResource();
            config.register(JacksonJsonProvider.class);

            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            server = new Server(new InetSocketAddress(ip, port));
            ServletContextHandler context = new ServletContextHandler(server, "/*");
            context.addServlet(servlet, "/*");

            CacheClusterViewerFactory.configure();
            CacheClusterService.INSTANCE.start();
            SubCacheService.INSTANCE.start();
            server.start();
            logger.info("started successfully.");
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", ExceptionUtils.getStackTrace(e)));
            throw e;
        }

    }

    @Override
    public void stop() throws Exception {
        logger.info("stopping...");
        try {
            server.stop();
            server.join();

            SubCacheService.INSTANCE.stop();
            CacheClusterService.INSTANCE.stop();
        } catch (Exception e) {
            logger.error(String.format("unexpected exception[%s]", ExceptionUtils.getStackTrace(e)));
            throw e;
        } finally {
            server.destroy();
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
