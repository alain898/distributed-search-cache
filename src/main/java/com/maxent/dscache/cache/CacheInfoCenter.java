package com.maxent.dscache.cache;

import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheInfoCenter {

    private String zookeepers;

    public void init(){
        /**
         * 1、从zookeeper读取以下信息:
         *          CacheGroup
         *          -> VirtualHost
         *          -> PhysicalHost
         *          -> Cache
         *          --> Partitions
         * 开始初始化之前,在zookeeper中设置标志并锁住,以保证状态信息的一致性。
         * 初始化完成后,清除标志并释放锁
         *
         * 2、当向cache集群中新增PhysicalHost时,默认按照各个CacheGroup的
         * (VirtualHost总数/PhysicalHost总数),来设置新增物理主机上VirtualHost的个数。
         * 当然也可以手工指定新增VirtualHost的个数。
         * 新增主机过程中,集群状态变更不会通知给客户端和服务器,而是在所有变更成功实施后,
         * 统一通知客户端和服务器进行状态切换;
         * 先更新服务器状态,服务器更新成功后,再通知客户端更新状态;
         * 服务器更新期间,所有客户端的请求都失败;
         * 如果是服务器切换失败,则回滚;
         * 如果是客户端切换失败,不回滚,但是客户端的后续任何与集群相关的操作都会失败,直到客户端成功更新消息;
          */
    }

    public IPartitioner getPartitioner(String cacheGroupName){
        return null;
    }

    public List<VirtualHost> getVirtualHosts(String cacheGroupName){
        return null;
    }
}
