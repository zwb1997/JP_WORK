package work.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * @author x
 */
@Component("ThreadProvider")
public class ThreadProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadProvider.class);
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_CORE_POOL_SIZE = 8;
    private static final long KEEP_ALIVE_TIME = 10 * 1000;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final LinkedBlockingDeque<Runnable> BLOCKING_DEQUE = new LinkedBlockingDeque<>(12);
    private static final ThreadFactory NAMED_FACTORY = new DefaultNamedFactory();
    private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_CORE_POOL_SIZE,
            KEEP_ALIVE_TIME, TIME_UNIT, BLOCKING_DEQUE, NAMED_FACTORY);

    public static boolean submitTask(Runnable task) {
        boolean flag = false;

        return flag;
    }
}
