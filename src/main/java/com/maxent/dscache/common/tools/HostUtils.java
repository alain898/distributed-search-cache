package com.maxent.dscache.common.tools;

import com.google.common.base.Preconditions;
import com.maxent.dscache.cache.Host;
import com.maxent.dscache.cache.exceptions.InvalidHostException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alain on 16/8/27.
 */
public class HostUtils {
    public static int DEFAULT_SERVICE_PORT = 5232;

    public static List<Host> parseHosts(List<String> hosts) {
        Preconditions.checkNotNull(hosts, "hosts is null");

        List<Host> hostList = new ArrayList<>();
        for (String host : hosts) {
            String[] splits = host.split(":");
            if (splits.length == 1) {
                hostList.add(new Host(splits[0], DEFAULT_SERVICE_PORT));
            } else if (splits.length == 2) {
                hostList.add(new Host(splits[0], Integer.parseInt(splits[1])));
            } else {
                throw new InvalidHostException("");
            }
        }
        return hostList;
    }
}
