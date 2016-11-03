# DSCache
DSCache(Distributed Search Cache) is designed like a simplified in-memory ElasticSearch. It's used to search some document from millions of documents in memory according matched degree.

There are some important components in a DSCache:

**CacheGroup** is a group of caches. It's designed as a namespace and used to limit caches size in the group. But the reason to invole in CacheGroup is to make enlarging cache size and partitions at runtime possible. This will be detailed when descibe the algorithem.

**Cache** is a search cache instance. It's stored in memory and contains thousands or millions of documents. A Cache is logically composed of many partitions according a partitioned code of a document. A Cache is physically composed of multiple Subcaches which may be distributed in different hosts.

**Subcache** is a physical part of a Cache. It's used to distribute a Cache on multiple hosts. A Subcache holds part of total partitions in a Cache.

**Partition** is a logical part of a Cache. A parition is implemented as a list of blocks now. A partition will hold part of documents which have the same partitioned code.

**Block** is one element of blocks list in a partition. The partition will add one block at tail and remove one block at head when it reaches the maximum partition capacity.

**Replication** is a physical relication of a Subcache. A Subcache may have multiple replicaitons. Only one replication is supported currently, but the extension point is ready.


# Install
DSCache is developped on java 8.

```
mvn clean && mvn package
```
**Note** some test cases will depend on a local Zookeeper server, so please start a Zookeeper server locally before make DSCache release package. You will get a tar.gz package as follow:

```
distributed-search-cache-1.0-SNAPSHOT-assembly.tar.gz
```
copy the tar.gz package to the install dirctory and extract it:
```
tar zxvf distributed-search-cache-1.0-SNAPSHOT-assembly.tar.gz
```

DSCache uses Zookeeper to coordinate the cluster and store cluster status, so Zookeeper should be prepared before start the DSCache cluster.

JSVC is used to daemonize DSCache, so JSVC also need to be installed.

Modify the confiuration file conf/application.conf according to the actual deployment.

```
server {
  ip = "${dscache server ip}"
  port = ${dscache server port}
  data_dir = "{dscache data directory}"
}

zookeeper {
  connection_url = "${zookeeper connection url}"
}
```
The dscache data directory will hold persistent data files, so make sure the dscache data persist directory is created and accessible.

```
# startup service
bin/startup.sh

# shutdown service
bin/shutdown.sh
```

please note that the pid file will be placed under /var/run/dscache, so make sure the directory is created and accessible.

please set the $JAVA_HOME, and make sure the JSVC path in bin/setenv.sh is set correctly.

```
# bin/setenv.sh

#!/bin/bash
...
JSVC_EXECUTABLE="/usr/lib/bigtop-utils/jsvc"
...
```

# Usage
**Create** a cache group:

	CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
	CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
	String cacheGroupName = "cache_group_test1";
	String entryClassName = "com.alain898.dscache.cache.TestCacheEntry";
	int cacheGroupCapacity = 256;
	int cachesNumber = 4;
	int subCachesPerCache = 2;
	int partitionsPerSubCache = 16;
	int blockCapacity = 100;
	int blocksPerPartition = 10;
	CreateCacheGroupResponse response = cacheGroupClient.create(
	        cacheGroupName, entryClassName, cacheGroupCapacity,
	        cachesNumber, subCachesPerCache, partitionsPerSubCache,
	        blockCapacity, blocksPerPartition);
	String result = JsonUtils.toJson(response);
	System.out.println(result);

**Save** an document to the cache group:

	CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
	CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
	TestCacheEntry testCacheEntry = new TestCacheEntry();
	testCacheEntry.setField1("field1");
	testCacheEntry.setField2("field2");
	CacheSaveResponse response = cacheGroupClient.save("cache_group_test1", testCacheEntry);
	String result = JsonUtils.toJson(response);
	System.out.println(result);


**Update** a cache group:

	CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
	CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
	CacheGroupUpdateResponse response = cacheGroupClient.update("cache_group_test1", 4);
	String result = JsonUtils.toJson(response);
	System.out.println(result);


**Delete** a cache group:

	CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
	CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
	CacheGroupDeleteResponse response = cacheGroupClient.delete("cache_group_test1");
	String result = JsonUtils.toJson(response);
	System.out.println(result);
