package oxgnaw.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oxgnaw.curator.election.ZeusManager;

public class PathCache {
    private static final Logger LOG = LoggerFactory.getLogger(PathCache.class);
    private static final String zkAdrr = "localhost:2181";
    private static final String PATH = "/bonree/test";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = null;
        PathChildrenCache cache = null;
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                case CHILD_ADDED: {
                    LOG.info("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                    break;
                }

                case CHILD_UPDATED: {
                    LOG.info("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                    break;
                }

                case CHILD_REMOVED: {
                    LOG.info("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                    break;
                }
                default:
                    LOG.warn("other events");
                }
            }
        };
        try {
            client = CuratorFrameworkFactory.newClient(zkAdrr, new ExponentialBackoffRetry(1000, 3));
            client.start();

            // in this example we will cache data. Notice that this is optional.
            cache = new PathChildrenCache(client, PATH, true);
            cache.getListenable().addListener(listener);
            cache.start();

            Thread.sleep(2000);
            // 1. client try to add a child to PATH
            String c1 = ZKPaths.makePath(PATH, "c1");
            LOG.info("client try to add a child [{}] to PATH:{}", c1, PATH);
            client.create().creatingParentContainersIfNeeded().forPath(c1, new byte[]{});

            // 2. client try to add a child to PATH
            String c2 = ZKPaths.makePath(PATH, "c2");
            LOG.info("client try to add a child [{}] to PATH:{}", c2, PATH);
            client.create().creatingParentContainersIfNeeded().forPath(c2, new byte[]{});
            // 3. client try to delete a child of PATH
            LOG.info("client try to delete a child [{}] of PATH:{}", c2, PATH);
            client.delete().forPath(c2);

            LOG.info("client try to delete a child [{}] of PATH:{}", c1, PATH);
            client.delete().forPath(c1);
            Thread.sleep(2000);

        } finally {
            CloseableUtils.closeQuietly(cache);
            CloseableUtils.closeQuietly(client);
        }
    }
}
