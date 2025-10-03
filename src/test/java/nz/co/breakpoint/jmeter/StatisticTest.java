package nz.co.breakpoint.jmeter;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

public class StatisticTest {

    @Test
    public void testConcurrency() throws InterruptedException {
        Metric max = Statistic.max();
        int n = 100, m = 100;
        Random r = new Random(System.currentTimeMillis());
        long[] values = LongStream.generate(() -> r.nextInt(Integer.MAX_VALUE)).limit(n*m).toArray();
        long expected = Arrays.stream(values).max().getAsLong();

        for (int i=0; i<n; i++) {
            final int offset = i*m;
            Thread t = new Thread(() -> {
                for (int j=0; j<m; j++) {
                    max.increment(values[offset+j]);
                    Thread.yield();
                }
            });
            t.start();
            t.join();
        }
        assertEquals(expected, max.getResult(), 0.0d);
    }
}
