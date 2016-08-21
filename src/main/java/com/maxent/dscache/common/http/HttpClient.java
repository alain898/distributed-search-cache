package com.maxent.dscache.common.http;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Created by alain on 16/8/21.
 */
// TODO: extract the IHttpClient interface
public class HttpClient {

    public <T> T post(String url, String path, Object request, Class<T> responseClass) {
        return post(url, path,
                MediaType.APPLICATION_JSON_TYPE, request,
                MediaType.APPLICATION_JSON_TYPE, responseClass);
    }

    public <T> T post(String url, String path,
                      MediaType requestMediaType, Object request,
                      MediaType responseMediaType, Class<T> responseClass) {
        Client client = ClientBuilder.newClient(new ClientConfig());

        return client.target(url)
                .path(path)
                .request(responseMediaType)
                .post(Entity.entity(request, requestMediaType), responseClass);
    }
}
