package oxgnaw;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TmpJunitTest {
    /**
     * 创建测试用的临时文件，在/tmp/junitxxxxxx/下
     */
    @Rule
    public TemporaryFolder tf = new TemporaryFolder();

    @Test
    public void testTmpFolder() throws IOException, InterruptedException {
        File file = tf.newFile("tmpJunit.txt");
        assert file.exists();
        System.out.println(file.getAbsolutePath());
    }
}
