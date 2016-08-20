package com.maxent.dscache.api.rest.controller;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

/**
 * Created by alain on 16/8/16.
 */
@Singleton
@Path("/hello")
public class HelloWorldController {
    @GET
    @Path("/{who}")
    public String hello(@Context final HttpServletResponse httpServletResponse,
                        @PathParam("who") final String who) {
        if (StringUtils.isNotBlank(who)) {
            return "hello " + who;
        } else {
            return "hello nobody";
        }

    }
}
