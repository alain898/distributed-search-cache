package com.maxent.dscache.cache;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.maxent.dscache.api.rest.RestResource;
import com.typesafe.config.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.net.InetSocketAddress;

/**
 * Created by alain on 16/9/23.
 */
public class ApiServer {
    private Server server;
    private String ip;
    private int port;

    public ApiServer(Config config) {
        ip = config.getString("server.ip");
        port = config.getInt("server.port");

        ResourceConfig resourceConfig = new RestResource();
        resourceConfig.register(JacksonJsonProvider.class);

        ServletHolder servlet = new ServletHolder(new ServletContainer(resourceConfig));
        server = new Server(new InetSocketAddress(ip, port));
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }
}
