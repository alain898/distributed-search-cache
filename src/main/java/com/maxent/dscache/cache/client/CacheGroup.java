package com.maxent.dscache.cache.client;

import com.maxent.dscache.common.partitioner.IPartitioner;

/**
 * Created by alain on 16/8/20.
 */
public class CacheGroup {
    private IPartitioner partitioner;

    private int virtualHostsNumber;

    public CacheGroup(IPartitioner partitioner, int virtualHostsNumber) {
        this.partitioner = partitioner;
        this.virtualHostsNumber = virtualHostsNumber;
    }

    public IPartitioner getPartitioner() {
        return partitioner;
    }

    public void setPartitioner(IPartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public int getVirtualHostsNumber() {
        return virtualHostsNumber;
    }

    public void setVirtualHostsNumber(int virtualHostsNumber) {
        this.virtualHostsNumber = virtualHostsNumber;
    }
}
