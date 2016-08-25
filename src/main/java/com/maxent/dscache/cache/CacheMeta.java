package com.maxent.dscache.cache;

import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheMeta {

    private String version;

    private String name;
    private IPartitioner partitioner;

    private String entryClassName;
    private Class<ICacheEntry> entryClass;

    private List<SubCacheMeta> subCacheMetas;
    private int partitionsPerSubCache;

}
