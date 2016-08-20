package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestAddHostsRequest;
import com.maxent.dscache.api.rest.response.RestAddHostsResponse;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by alain on 16/8/18.
 */
@Singleton
@Path("/management")
public class ManagementController {
    @POST
    @Path("/hosts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestAddHostsResponse addhosts(@Context final HttpServletResponse httpServletResponse,
                                         final RestAddHostsRequest request) {
        RestAddHostsResponse response = new RestAddHostsResponse();
        response.setMessage("success");
        return response;
    }
}
