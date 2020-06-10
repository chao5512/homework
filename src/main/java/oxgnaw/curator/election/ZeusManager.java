package oxgnaw.curator.election;

import java.io.Closeable;
import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oxgnaw.curator.TestZkServer;
import sun.rmi.runtime.Log;

public class ZeusManager extends LeaderSelectorListenerAdapter implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(ZeusManager.class);
    private String name;
    private LeaderSelector leaderSelector;

    public ZeusManager(CuratorFramework client, String path, String name) {
        this.name = name;

        leaderSelector = new LeaderSelector(client, path, this);

        // for most cases you will want your instance to requeue when it relinquishes leadership
        leaderSelector.autoRequeue();
    }

    public void start() {
        leaderSelector.start();
    }

    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        LOG.info("now [{}] is leader", name);
        Thread.sleep(10000);
        LOG.info("[{}] relinquishing leadership!", name);
    }

    public void close() throws IOException {
        leaderSelector.close();
    }

    public static void main(String[] args) throws InterruptedException {
        String zkAdrr = "localhost:12181";
        String path = "/examples/leader";
        CuratorFramework client = CuratorFrameworkFactory
            .newClient(zkAdrr, new ExponentialBackoffRetry(1000, 3));

        ZeusManager zeusManager = new ZeusManager(client, path, args[0]);
        client.start();
        zeusManager.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
