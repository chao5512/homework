package oxgnaw;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * 利用guava的suppliers实现单例模式
 * 这样是懒加载，只有调用supplier.get时采取加载
 */
public class SuppliersSingleInstance {
    class OM {
        String name;
        LocalDateTime initTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getInitTime() {
            return initTime;
        }

        public OM(String name) {
            this.name = name;
            initTime = LocalDateTime.now();
            System.out.println("construct time is " + initTime);
        }
    }
    class SingleSupplier implements Supplier<OM> {
        @Override
        public OM get() {
            return new OM(String.valueOf(System.currentTimeMillis()));
        }
    }

    @Test
    public void testGet(){
        SingleSupplier singleSupplier = new SingleSupplier();
        IntStream.range(0,4)
                 .forEach(x -> {
                     System.out.println(singleSupplier.get());
                 });

    }

    @Test
    public void testSingleGet(){
        SingleSupplier singleSupplier = new SingleSupplier();
        Supplier<OM> memoize = Suppliers.memoize(singleSupplier);

        IntStream.range(0, 4)
                 .forEach(x -> {
                     System.out.println(memoize.get());
                 });
    }

    @Test
    public void testInitTime() throws InterruptedException {
        SingleSupplier singleSupplier = new SingleSupplier();
        System.out.println("init Supplier complete at " + LocalDateTime.now());
        Supplier<OM> memoize = Suppliers.memoize(singleSupplier);
        LocalDateTime startGetting = LocalDateTime.now();
        //让结果明显
        Thread.sleep(2000);
        System.out.println("start getting at " + startGetting);
        LocalDateTime initTime = memoize.get().getInitTime();

        Assert.assertTrue(startGetting.isBefore(initTime));

    }
}
