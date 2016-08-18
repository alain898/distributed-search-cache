package com.maxent.dscache;

import com.maxent.dscache.api.rest.RestResource;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Properties;


/**
 * Created by alain on 16/7/19.
 */
public class Main implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_REST_SERVER_URL = "http://localhost/";
    private static final int DEFAULT_REST_SERVER_PORT = 2222;
    private Server server;

    private String url;
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
            url = props.getProperty("url", DEFAULT_REST_SERVER_URL);
            port = Integer.parseInt(props.getProperty("port", String.valueOf(DEFAULT_REST_SERVER_PORT)));

            URI baseUri = UriBuilder.fromUri(url).port(port).build();
            ResourceConfig config = new RestResource();
            config.register(JacksonJsonProvider.class);
            server = JettyHttpContainerFactory.createServer(baseUri, config);

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
