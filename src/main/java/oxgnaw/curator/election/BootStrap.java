package oxgnaw.curator.election;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

public class BootStrap {
    private static final String PATH = "/examples/leader";
    private static final int CLIENT_QTY = 2;
    private static final String zkAdrr = "localhost:12181";

    public static void main(String[] args) throws InterruptedException {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<ZeusManager> examples = Lists.newArrayList();
        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                CuratorFramework client = CuratorFrameworkFactory
                    .newClient(zkAdrr, new ExponentialBackoffRetry(1000, 3));
                clients.add(client);

                ZeusManager zeusManager = new ZeusManager(client, PATH, "Client #" + i);
                examples.add(zeusManager);

                client.start();
                zeusManager.start();
                Thread.sleep(Integer.MAX_VALUE);
            }
        } finally {
            System.out.println("Shutting down...");

            for (ZeusManager zeusManager : examples) {
                CloseableUtils.closeQuietly(zeusManager);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
