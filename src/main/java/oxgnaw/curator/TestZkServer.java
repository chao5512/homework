package oxgnaw.curator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestZkServer {
    private static final Logger LOG = LoggerFactory.getLogger(TestZkServer.class);
    private TestingServer zkServer;

    public void start() throws Exception {
        LOG.info("java.io.tmpdir = [{}]", System.getProperty("java.io.tmpdir"));
        Map<String, Object> customProperties = new HashMap();
        //customProperties.put("dataDir", "/home/wangchao/IdeaProjects/homework/dataDir");
        customProperties.put("authProvider.1", "org.apache.zookeeper.server.auth.SASLAuthenticationProvider");
        customProperties.put("kerberos.removeHostFromPrincipal", "true");
        customProperties.put("kerberos.removeRealmFromPrincipal", "true");
        String dataPath = "/home/wangchao/IdeaProjects/homework/dataDir";
        File dataDir = createTempDir(dataPath);
        InstanceSpec spec = new InstanceSpec(dataDir,
                                             12181,
                                             -1,
                                             -1,
                                             true,
                                             1,
                                             -1,
                                             -1,
                                             customProperties);
        zkServer = new TestingServer(spec, true);
    }

    public void close() throws IOException {
        zkServer.close();
    }

    public static void main(String[] args) throws Exception {
        TestZkServer testZkServer = new TestZkServer();
        testZkServer.start();
        if (testZkServer.checkStatus()) {
            //do somthing
            LOG.info("zk start successfully connect zk server through out the port [{}]", testZkServer.getPort());
        }
    }

    private int getPort() {
        return zkServer.getPort();
    }

    public boolean checkStatus() throws InterruptedException {
        Thread.sleep(2000);
        return true;
    }

    public static File createTempDir(String path) {
        File baseDir = new File(path);
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < 10000; ++counter) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }

        throw new IllegalStateException("Failed to create directory within 10000 attempts (tried "
                                            + baseName + "0 to " + baseName + 9999 + ')');
    }
}
