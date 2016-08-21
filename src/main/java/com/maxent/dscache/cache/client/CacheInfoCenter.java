package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.VirtualHost;
import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheInfoCenter {

    public IPartitioner getPartitioner(String cacheGroupName){
        return null;
    }

    public List<VirtualHost> getVirtualHosts(String cacheGroupName){
        return null;
    }
}
