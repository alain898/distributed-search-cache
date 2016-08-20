package com.maxent.dscache.api.rest.tools;

import com.maxent.dscache.api.rest.response.RestCommonResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by alain on 16/8/20.
 */
public class RestHelper {

    private static final Logger logger = LoggerFactory.getLogger(RestHelper.class);

    public static <T> T doResponse(final HttpServletResponse response,
                                   final int status,
                                   final T result) {
        setResponseStatus(response, status);
        return result;
    }

    public static void setResponseStatus(final HttpServletResponse response, final int status) {
        response.setStatus(status);
        try {
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("failed to flush response", e);
        }
    }

    public static <T extends RestCommonResponse> T createErrorResponse(Class<T> clazz, String info) {

        try {
            T instance = clazz.newInstance();
            instance.setError(info);
            return instance;
        } catch (Exception e) {
            logger.error(String.format(
                    "failed to createErrorResponse, clazz[%s], info[%s], exception[%s]",
                    String.valueOf(clazz), info, ExceptionUtils.getStackTrace(e)));
            return null;
        }
    }
}
