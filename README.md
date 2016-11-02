# DSCache
DSCache(Distributed Search Cache) is designed like a simplified in-memory ElasticSearch. It's used to search some document from millions of documents in memory according matched degree.

There are some import components in a DSCache:

**CacheGroup** is a group of caches. It's designed as a namespace and used to limit caches size in the group. But the cause reason to invole in CacheGroup is to make enlarging cache size and partitons at runtime possible. This will be detailed when descibe the algorithem.

**Cache** is a search cache instance. It's stored in memory and contains thousands or millions of documents. A Cache is logically composed of many partitions according a partitioned code of a document. A Cache is physically composed of multiple Subcaches which may distributed in different hosts.

**Subcache** is a physical part of a Cache. It's used to distribute a Cache on multiple hosts. A Subcache hold parts of total partitions in a Cache.

**Partition** is a logical part of a Cache. A parition is implemented as a list of blocks now. A partition will hold part of documents which has the same paritioned code.

**Block** is one element of blocks list in a partition. The partition will add one block at tail and remove one block at head when it reaches the maximum parition capacity.

**Replication** is a physical relication of a Subcache. A Subcache may have multiple replicaitons. Only one replication is supported currently, but the extension point is ready.
