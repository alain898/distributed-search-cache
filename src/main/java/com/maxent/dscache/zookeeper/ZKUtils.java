package com.maxent.dscache.zookeeper;

import org.apache.zookeeper.*;

/**
 * Created by alain on 16/8/22.
 */
public class ZKUtils {

    public static void main(String[] args) throws Exception {
        String connectString = "127.0.0.1:2181";
        int sessionTimeout = 10000;
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(String.format(
                        "\ntest watcher, type[%s], path[%s], state[%s], wrapper[%s], toString[%s]",
                        watchedEvent.getType(),
                        watchedEvent.getPath(),
                        watchedEvent.getState(),
                        watchedEvent.getWrapper(),
                        watchedEvent.toString()));
            }
        };
        ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, watcher);

        if (zk.exists("/test", true) == null) {
            zk.create("/test", "p_data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        // create
        if (zk.exists("/test/path1", true) == null) {
            zk.create("/test/path1", "p_data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }

        // read
        zk.getChildren("/test/path1", true);
        zk.getData("/test/path1", true, null);
        zk.exists("/test/path1", true);

        // update
        zk.setData("/test/path1", "new_data".getBytes(), -1);

//        Thread.sleep(20000);

        // delete
//        zk.delete("/test/path1", -1);

        zk.close();
//        Thread.sleep(3000);

        System.out.println("success");

    }
}
